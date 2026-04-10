package com.quyen.shoplite.unit;

import com.quyen.shoplite.domain.*;
import com.quyen.shoplite.domain.request.ReqOrderDTO;
import com.quyen.shoplite.domain.request.ReqOrderItemDTO;
import com.quyen.shoplite.domain.response.ResOrderDTO;
import com.quyen.shoplite.repository.*;
import com.quyen.shoplite.service.OrderService;
import com.quyen.shoplite.util.error.IdInvalidException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class OrderServiceTest {

    @Mock private OrderRepository orderRepository;
    @Mock private OrderItemsRepository orderItemsRepository;
    @Mock private UserRepository userRepository;
    @Mock private ProductRepository productRepository;
    @Mock private InventoryLogsRepository inventoryLogsRepository;

    @InjectMocks
    private OrderService orderService;

    private User mockUser;
    private Product mockProduct;

    @BeforeEach
    void setUp() {
        mockUser = User.builder()
                .id(1).username("user1").isActive(true).build();

        mockProduct = Product.builder()
                .id(1).name("Laptop").sku("LAP-001")
                .price(1500.0).stock(10).isDeleted(false).build();
    }

    private ReqOrderDTO buildOrderRequest(int productId, long qty, double price, double discount) {
        ReqOrderItemDTO item = new ReqOrderItemDTO();
        item.setProductId(productId);
        item.setQuantity(qty);
        item.setPrice(price);

        ReqOrderDTO req = new ReqOrderDTO();
        req.setUserId(1);
        req.setDiscount(discount);
        req.setItems(List.of(item));
        return req;
    }

    @Test
    @DisplayName("✅ Tạo đơn hàng thành công, stock bị trừ")
    void create_Success_StockDecremented() {
        ReqOrderDTO req = buildOrderRequest(1, 2, 1500.0, 0);

        when(userRepository.findById(1)).thenReturn(Optional.of(mockUser));
        when(productRepository.findById(1)).thenReturn(Optional.of(mockProduct));

        Order savedOrder = Order.builder()
                .id(1).code("ORD-ABCD1234").totalAmount(3000.0).build();
        when(orderRepository.save(any())).thenReturn(savedOrder);
        when(orderItemsRepository.save(any())).thenAnswer(inv -> inv.getArgument(0));
        when(productRepository.save(any())).thenAnswer(inv -> inv.getArgument(0));

        ResOrderDTO result = orderService.create(req);

        assertThat(result).isNotNull();
        // Stock phải giảm từ 10 → 8
        assertThat(mockProduct.getStock()).isEqualTo(8);
        verify(inventoryLogsRepository).save(any());
    }

    @Test
    @DisplayName("✅ Tổng tiền tính đúng (quantity × price - discount)")
    void create_TotalAmount_CalculatedCorrectly() {
        ReqOrderDTO req = buildOrderRequest(1, 3, 1000.0, 200.0);

        when(userRepository.findById(1)).thenReturn(Optional.of(mockUser));
        when(productRepository.findById(1)).thenReturn(Optional.of(mockProduct));

        Order savedOrder = Order.builder()
                .id(1).code("ORD-TEST").totalAmount(2800.0).build(); // 3*1000 - 200
        when(orderRepository.save(any())).thenReturn(savedOrder);
        when(orderItemsRepository.save(any())).thenAnswer(inv -> inv.getArgument(0));
        when(productRepository.save(any())).thenAnswer(inv -> inv.getArgument(0));

        orderService.create(req);

        verify(orderRepository).save(argThat(o -> o.getTotalAmount() == 2800.0));
    }

    @Test
    @DisplayName("❌ Tồn kho không đủ → ném IdInvalidException")
    void create_InsufficientStock_ThrowsException() {
        mockProduct.setStock(1);  // chỉ còn 1
        ReqOrderDTO req = buildOrderRequest(1, 5, 1500.0, 0);  // đặt 5

        when(userRepository.findById(1)).thenReturn(Optional.of(mockUser));
        when(productRepository.findById(1)).thenReturn(Optional.of(mockProduct));

        assertThatThrownBy(() -> orderService.create(req))
                .isInstanceOf(IdInvalidException.class)
                .hasMessageContaining("không đủ tồn kho");

        verify(orderRepository, never()).save(any());
    }

    @Test
    @DisplayName("❌ User không tồn tại → ném IdInvalidException")
    void create_UserNotFound_ThrowsException() {
        when(userRepository.findById(99)).thenReturn(Optional.empty());

        ReqOrderDTO req = buildOrderRequest(1, 1, 100.0, 0);
        req.setUserId(99);

        assertThatThrownBy(() -> orderService.create(req))
                .isInstanceOf(IdInvalidException.class)
                .hasMessageContaining("99");
    }

    @Test
    @DisplayName("❌ Product không tồn tại → ném IdInvalidException")
    void create_ProductNotFound_ThrowsException() {
        when(userRepository.findById(1)).thenReturn(Optional.of(mockUser));
        when(productRepository.findById(99)).thenReturn(Optional.empty());

        ReqOrderDTO req = buildOrderRequest(99, 1, 100.0, 0);

        assertThatThrownBy(() -> orderService.create(req))
                .isInstanceOf(IdInvalidException.class)
                .hasMessageContaining("99");
    }
}
