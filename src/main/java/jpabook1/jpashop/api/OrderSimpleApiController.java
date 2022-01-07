package jpabook1.jpashop.api;

import jpabook1.jpashop.domain.Address;
import jpabook1.jpashop.domain.Order;
import jpabook1.jpashop.domain.OrderStatus;
import jpabook1.jpashop.repository.OrderRepository;
import jpabook1.jpashop.repository.OrderSearch;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequiredArgsConstructor
public class OrderSimpleApiController {

    private final OrderRepository orderRepository;

    /**
     * V1. 엔티티 직접 노출
     * - Hibernate5Module 모듈 등록과 @JsonIgnore 하는 방법이 존재
     * 그러나 이 방법은 절대로 사용 x
     */
    @GetMapping("/api/v1/simple-orders")
    public List<Order> odersV1(){
        List<Order> all = orderRepository.findAllByString(new OrderSearch());
        for(Order order:all){
            order.getMember().getName();
            order.getDelivery().getAddress();
        }
        return all;
    }
    /**
     * V2. 엔티티를 조회해서 DTO로 변환(fetch join 사용 x)
     * - 단점 : 지연로딩으로 쿼리 N번 호출(N+1 문제)
     */
    @GetMapping("api/v2/simple-orders")
    public List<SimpleOrderDto> ordersV2(){
        return orderRepository.findAllByString(new OrderSearch()).stream()
                .map(m -> new SimpleOrderDto(m))
                .collect(Collectors.toList());
    }

    /**
     * V3. 엔티티를 조회해서 DTO로 변환(fetch join 사용 o)
     * - fetch join 으로 쿼리 1번만 조회
     * 참고 : fetch join에 대한 자세한 내용은 기본편 참고(매우매우매우 중요)
     */
    @GetMapping("/api/v3/simple-orders")
    public List<SimpleOrderDto> ordersV3(){
        return orderRepository.findAllWithMemberDelivery().stream()
                .map(m -> new SimpleOrderDto(m))
                .collect(Collectors.toList());
    }

    @Data
    static class SimpleOrderDto{
        private Long orderId;
        private String name;
        private LocalDateTime orderDate;
        private OrderStatus orderStatus;
        private Address address;

        public SimpleOrderDto(Order order){
            orderId = order.getId();
            name = order.getMember().getName();
            orderDate=order.getOrderDate();
            orderStatus=order.getStatus();
            address = order.getMember().getAddress();
        }
    }
}
