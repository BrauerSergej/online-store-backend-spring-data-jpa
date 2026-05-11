package de.ait.g_67_shop.service;

import de.ait.g_67_shop.domain.Customer;
import de.ait.g_67_shop.domain.Position;
import de.ait.g_67_shop.domain.Product;
import de.ait.g_67_shop.repository.CustomerRepository;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;
import java.util.Set;

@Service
public class CustomerServiceImpl implements CustomerService {

    // В этом классе будет храниться ссылка на CustomerRepository.
    // Через неё CustomerServiceImpl будет работать с покупателями в базе данных.
    private final CustomerRepository repository;
    private final ProductService productService;

    public CustomerServiceImpl(CustomerRepository repository, ProductService productService) {
        this.repository = repository;
        this.productService = productService;
    }

    @Override
    // Этот метод будет вызваться в CustomerController
    // При сохранении покупатель автоматически считается активным.
    public Customer save(Customer customer) {
        // Делаем клиента активным
        customer.setActive(true);
        // Методы технологии Spring Data JPA (save)
        // Метод save сам сохранит клиента в базу данных и присвоит тот ID
        // который присвоила база данных в результате мы возвращаем клиента
        // с присвоенным идентификатором
        return repository.save(customer);
    }

    // Вернуть всех активных покупателей.
    @Override
    public List<Customer> getAllActiveCustomers() {
        // Более эффективный способ мы можем сразу из базы запросить только активные продукты
        return repository.findAllByActiveTrue();
    }

    // Вернуть одного активного покупателя по id.
    @Override
    public Customer getActiveCustomerById(Long id) {
        return repository.findByIdAndActiveTrue(id).orElseThrow(
                // Временная обработка ошибки до тех пор, пока
                // не изучим соответствующую тему
                () -> new IllegalArgumentException("Customer not found with id " + id)
        );
    }

    @Override
    @Transactional // Трансакция с базой будет открыто пока работает метод - пока работает метод клиент всё время будет
    // находиться в состоянии Managed, управляемом. А значит, когда мы засетим ему новую цену,
    // технология Spring Data JPA эти изменения отразит ещё и в базе данных, а не только в самом Java-объекте.
    // Изменить одного покупателя по id.
    public void update(Long id, Customer customer) {
        String newName = customer.getName();
        repository.findById(id).ifPresent(x -> x.setName(newName));
    }

    @Override
    @Transactional
    public void deleteById(Long id) {
        repository.findByIdAndActiveTrue(id).ifPresent(x -> x.setActive(false));
    }

    @Override
    @Transactional
    public void restoreById(Long id) {
        repository.findById(id).ifPresent(x -> x.setActive(true));
    }

    // * Вернуть общее количество покупателей в базе данных (активных).
    @Override
    public int getAllActiveCustomerCount() {
        return repository.countByActiveTrue();
    }

    // * Вернуть стоимость корзины покупателя по его идентификатору (если он активен).
    @Override
    @Transactional
    public BigDecimal getActiveCustomerCartTotalCostById(Long id) {
        Customer customer = getActiveCustomerById(id);

        if (customer == null || customer.getCart() == null || customer.getCart().getPositions().isEmpty()) {
            return BigDecimal.ZERO;
        }

        // Умножаем цену продукта на его количество в позиции и суммируем
        return customer.getCart().getPositions().stream()
                .map(p -> p.getProduct().getPrice().multiply(BigDecimal.valueOf(p.getQuantity())))
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }


    @Override
    @Transactional
    public BigDecimal getAverageProductPriceInCartByCustomerId(Long id) {
        Customer customer = getActiveCustomerById(id);

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
    public void addProductToCart(Long customerId, Long productId, int quantity) {
        Customer customer = getActiveCustomerById(customerId);
        Product product = productService.getActiveProductById(productId);

        if (customer != null && product != null && customer.getCart() != null) {
            Set<Position> positions = customer.getCart().getPositions();

            // Ищем, есть ли уже такая позиция с этим продуктом в корзине
            Position existingPosition = positions.stream()
                    .filter(p -> p.getProduct().getId().equals(productId))
                    .findFirst()
                    .orElse(null);

            if (existingPosition != null) {
                // Если товар уже в корзине, просто увеличиваем количество
                existingPosition.setQuantity(existingPosition.getQuantity() + quantity);
            } else {
                // Если товара еще нет, создаем новую позицию
                Position newPosition = new Position();
                newPosition.setCart(customer.getCart());
                newPosition.setProduct(product);
                newPosition.setQuantity(quantity);

                positions.add(newPosition);
            }
        }
    }

    @Override
    @Transactional
    public void removeProductFromCartById(Long customerId, Long productId, int quantity) {
        Customer customer = getActiveCustomerById(customerId);

        if (customer != null && customer.getCart() != null) {
            Set<Position> positions = customer.getCart().getPositions();

            Position existingPosition = positions.stream()
                    .filter(p -> p.getProduct().getId().equals(productId))
                    .findFirst()
                    .orElse(null);

            if (existingPosition != null) {
                int newQuantity = existingPosition.getQuantity() - quantity;
                if (newQuantity > 0) {
                    existingPosition.setQuantity(newQuantity);
                } else {
                    // Если удаляем всё (или больше чем было), убираем позицию совсем
                    positions.remove(existingPosition);
                }
            }
        }
    }

    @Override
    @Transactional
    public void clearCustomerCartById(Long id) {
        Customer customer = getActiveCustomerById(id);

        if (customer != null && customer.getCart() != null) {
            customer.getCart().getPositions().clear();
        }
    }
}
