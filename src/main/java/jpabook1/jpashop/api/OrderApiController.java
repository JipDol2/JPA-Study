package jpabook1.jpashop.api;

import jpabook1.jpashop.domain.Address;
import jpabook1.jpashop.domain.Order;
import jpabook1.jpashop.domain.OrderItem;
import jpabook1.jpashop.domain.OrderStatus;
import jpabook1.jpashop.repository.OrderRepository;
import jpabook1.jpashop.repository.OrderSearch;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.aspectj.weaver.ast.Or;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequiredArgsConstructor
public class OrderApiController {

    private final OrderRepository orderRepository;

    /**
     * V1. 엔티티 직접 노출
     * - Hibernate5Module 모듈 등록, LAZY=null 처리
     * - 양방향 관계 문제 발생 -> @JsonIgnore
     * - 위의 것들을 처리 안하면 무한루프 문제 발생
     */
    @GetMapping("/api/v1/orders")
    public List<Order> ordersV1(){
        List<Order> all = orderRepository.findAllByString(new OrderSearch());
        for (Order order : all) {
            order.getMember().getName();
            order.getDelivery().getAddress();

            order.getOrderItems()
                    .stream()
                    .forEach(o->o.getItem().getName());
        }
        return all;
    }

    /**
     * V2. 엔티티를 조회해서 DTO로 변환(fetch join 사용 x)
     */
    @GetMapping("/api/v2/orders")
    public List<OrderDto> ordersV2(){
        List<Order> orders = orderRepository.findAllByString(new OrderSearch());
        List<OrderDto> collect = orders.stream()
                .map(o -> new OrderDto(o))
                .collect(Collectors.toList());
        return collect;
    }

    @Data
    static class OrderDto{

        private Long orderId;
        private String name;
        private LocalDateTime orderDate;
        private OrderStatus orderStatus;
        private Address address;
        //private List<OrderItem> orderItems;
        private List<OrderItemDto> orderItems;

        public OrderDto(Order order) {
            orderId = order.getId();
            name = order.getMember().getName();
            orderDate=order.getOrderDate();
            address = order.getMember().getAddress();
//            order.getOrderItems().stream().forEach(o->o.getItem().getName());     => 이렇게 하면 절대 안된다. 왜냐면 OrderItem 자체가 Entity 이기 때문이다.
//            orderItems = order.getOrderItems();                                       따라서 OrderItem도 DTO를 만들어야 된다.
            orderItems = order.getOrderItems().stream()
                    .map(o->new OrderItemDto(o))
                    .collect(Collectors.toList());
        }
    }
    @Data
    static class OrderItemDto{
        private String itemName; //상품명
        private int orderPrice; //주문 가격
        private int count;      //주문 수량

        public OrderItemDto(OrderItem orderItem){
            itemName = orderItem.getItem().getName();
            orderPrice = orderItem.getOrderPrice();
            count = orderItem.getCount();
        }
    }
}