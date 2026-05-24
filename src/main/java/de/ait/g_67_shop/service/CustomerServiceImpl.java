package de.ait.g_67_shop.service;

import de.ait.g_67_shop.domain.Cart;
import de.ait.g_67_shop.domain.Customer;
import de.ait.g_67_shop.domain.Position;
import de.ait.g_67_shop.domain.Product;
import de.ait.g_67_shop.dto.customer.CustomerDto;
import de.ait.g_67_shop.dto.customer.CustomerSaveDto;
import de.ait.g_67_shop.dto.customer.CustomerUpdateDto;
import de.ait.g_67_shop.dto.mapping.CustomerMapper;
import de.ait.g_67_shop.dto.position.PositionUpdateDto;
import de.ait.g_67_shop.exceptions.types.EntityNotFoundException;
import de.ait.g_67_shop.exceptions.types.EntityUpdateException;
import de.ait.g_67_shop.repository.CustomerRepository;
import jakarta.transaction.Transactional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;
import java.util.Objects;
import java.util.Set;

@Service
public class CustomerServiceImpl implements CustomerService {

    private final Logger logger = LoggerFactory.getLogger(CustomerServiceImpl.class);

    // В этом классе будет храниться ссылка на CustomerRepository.
    // Через неё CustomerServiceImpl будет работать с покупателями в базе данных.
    private final CustomerRepository repository;
    private final ProductService productService;
    private final CustomerMapper mapper;

    public CustomerServiceImpl(CustomerRepository repository, ProductService productService, CustomerMapper mapper) {
        this.repository = repository;
        this.productService = productService;
        this.mapper = mapper;
    }

    @Override
    // Этот метод будет вызваться в CustomerController
    // При сохранении покупатель автоматически считается активным.
    @Transactional
    public CustomerDto save(CustomerSaveDto saveDto) {
        // Если у вас логика метода допускает, что на вход пришло значение null, тогда вам эта защита не нужна.
        // Но если логика метода сама по себе такая, что вы не должны там работать со значением null, то такая защита нужна.
        Objects.requireNonNull(saveDto, "CustomerSaveDto cannot be null");
        Customer entity = mapper.mapDtoToEntity(saveDto);
        entity.setActive(true);
        // Автоматически создаём пустую корзину для нового клиента.
        // Связь @OneToOne(cascade = ALL) в Customer сохранит корзину каскадно.
        Cart cart = new Cart();
        cart.setCustomer(entity);
        entity.setCart(cart);

        repository.save(entity);
        logger.info("Business Event: New customer saved successfully with ID: {}", entity.getId());
        return mapper.mapEntityToDto(entity);
    }

    // Вернуть всех активных покупателей.
    @Override
    public List<CustomerDto> getAllActiveCustomers() {
        List<Customer> entities = repository.findAllByActiveTrue();
        return mapper.mapEntitiesToDto(entities);
    }

    @Override
    public CustomerDto getActiveCustomerById(Long id) {
        Customer customer = getActiveEntityById(id);
        return mapper.mapEntityToDto(customer);
    }

    // Вернуть одного активного покупателя по id.
    @Override
    public Customer getActiveEntityById(Long id) {
        Objects.requireNonNull(id, "Customer id cannot be null");
        return repository.findByIdAndActiveTrue(id).orElseThrow(
                // Метод ищет объект в базе? → используй orElseThrow с EntityNotFoundException.
                () -> new EntityNotFoundException(Customer.class, id)
        );
    }

    @Override
    @Transactional // Трансакция с базой будет открыто пока работает метод - пока работает метод клиент всё время будет
    // находиться в состоянии Managed, управляемом. А значит, когда мы засетим ему новую цену,
    // технология Spring Data JPA эти изменения отразит ещё и в базе данных, а не только в самом Java-объекте.
    // Изменить одного покупателя по id.
    public void update(Long id, CustomerUpdateDto updateDto) {
        // ставим для параметров, которые пришли в метод снаружи
        Objects.requireNonNull(id, "Customer id cannot be null");
        Objects.requireNonNull(updateDto, "CustomerUpdateDto cannot be null");
        String newName = updateDto.getName();
        if (newName == null || newName.isBlank()) {
            // Метод проверяет бизнес-правило? → используй throw new EntityUpdateException.
            // EntityUpdateException означает: объект может существовать, но операция невозможна
            // из-за неправильных данных или запрещённого действия.
            throw new EntityUpdateException("Customer name cannot be empty");
        }

        Customer customer = repository.findById(id).orElseThrow(
                // Метод ищет объект в базе? → используй orElseThrow с EntityNotFoundException.
                () -> new EntityNotFoundException(Customer.class, id)
        );
        customer.setName(newName);
        logger.info("Customer id {} updated, new name: {}", id, newName);
    }

    @Override
    @Transactional
    public void deleteById(Long id) {
        Objects.requireNonNull(id, "Customer id cannot be null");
        repository.findByIdAndActiveTrue(id).ifPresent(x -> {
            x.setActive(false);
            logger.info("Customer id {} marked as inactive", id);
        });
    }

    @Override
    @Transactional
    public void restoreById(Long id) {
        Objects.requireNonNull(id, "Customer id cannot be null");

        Customer customer = repository.findById(id).orElseThrow(
                // Метод ищет объект в базе? → используй orElseThrow с EntityNotFoundException.
                () -> new EntityNotFoundException(Customer.class, id)
        );
        customer.setActive(true);
        logger.info("Customer id {} marked as active", id);

    }

    // * Вернуть общее количество покупателей в базе данных (активных).
    @Override
    public int getAllActiveCustomerCount() {
        return repository.countByActiveTrue();
    }

    // * Вернуть стоимость корзины покупателя по его идентификатору (если он активен).
    @Override
    public BigDecimal getActiveCustomerCartTotalCostById(Long id) {
        CustomerDto customer = getActiveCustomerById(id);

        // Умножаем цену продукта на его количество в позиции и суммируем
        return customer.getCart().getPositions().stream()
                .map(p -> p.getProduct().getPrice().multiply(BigDecimal.valueOf(p.getQuantity())))
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }


    @Override
    public BigDecimal getAverageProductPriceInCartByCustomerId(Long id) {
        Customer customer = getActiveEntityById(id);

        if (customer == null || customer.getCart() == null || customer.getCart().getPositions().isEmpty()) {
            return BigDecimal.ZERO;
        }

        BigDecimal totalCost = getActiveCustomerCartTotalCostById(id);

        // Считаем общее количество всех товаров в корзине (сумма quantity)
        int totalItemsCount = customer.getCart().getPositions().stream()
                .mapToInt(Position::getQuantity)
                .sum();

        if (totalItemsCount == 0) return BigDecimal.ZERO;

        return totalCost.divide(BigDecimal.valueOf(totalItemsCount), 2, RoundingMode.HALF_UP);
    }

    @Override
    @Transactional
    public void addProductToCart(Long customerId, Long productId, PositionUpdateDto dto) {
        Objects.requireNonNull(customerId, "Customer id cannot be null");
        Objects.requireNonNull(productId, "Product id cannot be null");
        Objects.requireNonNull(dto, "PositionDto cannot be null");
        // dto.getQuantity() == null - quantity не пришёл / quantity пустой / quantity равен null
        // dto.getQuantity() <= 0 - Нормальное ли количество? Больше ли оно 0?
        if (dto.getQuantity() == null || dto.getQuantity() <= 0) {
            // Метод проверяет бизнес-правило? → используй throw new EntityUpdateException.
            // EntityUpdateException означает: объект может существовать, но операция невозможна
            // из-за неправильных данных или запрещённого действия.
            throw new EntityUpdateException("Position quantity should be greater than 0");
        }

        int quantity = dto.getQuantity();

        Customer customer = getActiveEntityById(customerId);
        Product product = productService.getActiveEntityById(productId);

        Set<Position> positions = customer.getCart().getPositions();

        Position existingPosition = positions.stream()
                .filter(p -> p.getProduct().getId().equals(productId))
                .findFirst()
                .orElse(null);

        if (existingPosition != null) {
            existingPosition.setQuantity(existingPosition.getQuantity() + quantity);
            logger.info("Business Event: Customer ID {} increased quantity of Product ID {} by {}. New quantity: {}",
                    customerId, productId, quantity, existingPosition.getQuantity());
        } else {
            Position newPosition = new Position();
            newPosition.setCart(customer.getCart());
            newPosition.setProduct(product);
            newPosition.setQuantity(quantity);

            positions.add(newPosition);
            logger.info("Business Event: Customer ID {} added NEW Product ID {} to cart. Quantity: {}",
                    customerId, productId, quantity);
        }
    }

    @Override
    @Transactional
    public void removeProductFromCartById(Long customerId, Long productId, PositionUpdateDto dto) {
        Objects.requireNonNull(customerId, "Customer id cannot be null");
        Objects.requireNonNull(productId, "Product id cannot be null");
        Objects.requireNonNull(dto, "PositionUpdateDto cannot be null");

        if (dto.getQuantity() == null || dto.getQuantity() <= 0) {
            // throw прерывает выполнение текущего метода.
            // EntityUpdateException сообщает, что операция изменения невозможна,
            // потому что клиент передал некорректное количество товара.
            // Далее эту ошибку перехватит GlobalExceptionHandler и вернёт клиенту HTTP 400 BAD_REQUEST.
            throw new EntityUpdateException("Quantity must be positive");
        }

        int quantity = dto.getQuantity();

        Customer customer = getActiveEntityById(customerId);
        Set<Position> positions = customer.getCart().getPositions();

        Position existingPosition = positions.stream()
                .filter(p -> p.getProduct().getId().equals(productId))
                .findFirst()
                .orElseThrow(() -> new EntityUpdateException("Product is not in customer's cart"));

        int newQuantity = existingPosition.getQuantity() - quantity;
        if (newQuantity > 0) {
            existingPosition.setQuantity(newQuantity);
            logger.info("Business Event: Customer ID {} reduced quantity of Product ID {} in cart by {}. Remaining: {}",
                    customerId, productId, quantity, newQuantity);
        } else {
            positions.remove(existingPosition);
            logger.info("Business Event: Customer ID {} COMPLETELY REMOVED Product ID {} from cart",
                    customerId, productId);
        }

    }

    @Override
    @Transactional
    public void clearCustomerCartById(Long id) {
        Customer customer = getActiveEntityById(id);
        Cart cart = customer.getCart();

        if (cart == null) {
            // Метод проверяет бизнес-правило? → используй throw new EntityUpdateException.
            // EntityUpdateException означает: объект может существовать, но операция невозможна
            // из-за неправильных данных или запрещённого действия.
            throw new EntityUpdateException("Customer cart does not exist");
        }
        // Возьми из корзины коллекцию всех позиций и сохрани её в переменную positions.
        Set<Position> positions = cart.getPositions();
        // считает, сколько позиций было в корзине.
        int itemsCount = positions.size();
        // Очищаем корзину
        positions.clear();

        // Бизнес-лог: фиксируем факт полной очистки и сколько позиций там было
        logger.info("Business Event: Customer ID {} CLEARED their cart. Removed {} unique product positions",
                id, itemsCount);
    }
}

// EntityNotFoundException  → 404 NOT_FOUND
// EntityUpdateException   → 400 BAD_REQUEST
// ConstraintViolation     → 400 BAD_REQUEST
// NullPointerException    → 500 INTERNAL_SERVER_ERROR