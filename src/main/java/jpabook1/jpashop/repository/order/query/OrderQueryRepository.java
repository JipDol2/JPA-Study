package jpabook1.jpashop.repository.order.query;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import org.springframework.web.bind.annotation.RequestParam;

import javax.persistence.EntityManager;
import java.util.List;

/**
 * Order Entity 자체를 조회하고 싶을때는 OrderRepository를 통하는 것이고
 * API 라던지 화면에 의존관계가 있는 애들을 떼어내려고 만든 것이다.
 * 즉, 핵심 비지니스 로직과 화면의 요청에 의한 로직을 분리하기 위해서 패키지를 따로 만든 것이다.
 */
@Repository
@RequiredArgsConstructor
public class OrderQueryRepository {

    private final EntityManager em;

    public List<OrderQueryDto> findOrderQueryDto() {
        List<OrderQueryDto> result = findOrders();
        result.forEach(o->{
            List<OrderItemQueryDto> orderItems = findOrderItems(o.getOrderId());
            o.setOrderItems(orderItems);
        });
        return result;
    }

    private List<OrderItemQueryDto> findOrderItems(Long orderId){
        return em.createQuery(
                "select new jpabook1.jpashop.repository.order.query.OrderItemQueryDto(oi.order.id,i.name,oi.orderPrice,oi.count)"+
                        " from OrderItem oi"+
                        " join oi.item i"+
                        " where oi.order.id = :orderId",OrderItemQueryDto.class)
                .setParameter("orderId",orderId)
                .getResultList();
    }
    private List<OrderQueryDto> findOrders() {
        return em.createQuery(
                "select new jpabook1.jpashop.repository.order.query.OrderQueryDto(o.id,m.name,o.orderDate,o.status,d.address)"+
                        " from Order o"+
                        " join o.member m"+
                        " join o.delivery d",OrderQueryDto.class
        ).getResultList();
    }
}