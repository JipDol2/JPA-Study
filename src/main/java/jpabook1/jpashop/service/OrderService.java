package jpabook1.jpashop.service;

import jpabook1.jpashop.domain.Delivery;
import jpabook1.jpashop.domain.Member;
import jpabook1.jpashop.domain.Order;
import jpabook1.jpashop.domain.OrderItem;
import jpabook1.jpashop.domain.item.Item;
import jpabook1.jpashop.repository.ItemRepository;
import jpabook1.jpashop.repository.MemberRepository;
import jpabook1.jpashop.repository.OrderRepository;
import jpabook1.jpashop.repository.OrderSearch;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class OrderService {

    private final OrderRepository orderRepository;
    private final MemberRepository memberRepository;
    private final ItemRepository itemRepository;
    /**
     * 주문
     */
    @Transactional
    public Long order(Long memberId,Long itemId,int count){

        //엔티티 조회
        Member member = memberRepository.findOne(memberId);
        Item item = itemRepository.findOne(itemId);

        //배송정보 조회
        Delivery delivery = new Delivery();
        delivery.setAddress(member.getAddress());

        //주문 상품 생성
        OrderItem orderItem = OrderItem.createOrderItem(item,item.getPrice(),count);

        /**
         * 밑에 코드는 에러가 발생한다. 그 이유는 OrderItem 의 기본 생성자를 proteced로 막아놓았기 때문이다.
         * 왜 이렇게 할까?
         * createOrderItem 함수를 이용해서 값들을 넣어주었다는 것은 OrderItem 객체를 생성하여 set 으로 값들을 넣어주고 싶지 않다는 뜻이다.
         * 그러면 '우리는 객체를 생성하지 않을거에요' 라고 알려주고 '제약'하기 위해서 proteced로 생성자를 막아놓는 것이다.
         * 이렇게 항상 사용하지 않을거라는 의견을 코드로 표현하고 제약하는 습관을 들어야 된다.
         */
        //OrderItem orderItem1 = new OrderItem();

        //주문 생성
        Order order = Order.createOrder(member,delivery,orderItem);

        //주문 저장
        orderRepository.save(order);    //casecade 때문에 이것만 해줘도 된다...

        return order.getId();
    }
    /**
     * 주문 취소
     */
    @Transactional
    public void cancelOrder(Long orderId){
        //주문 엔티티 조회
        Order order = orderRepository.findOne(orderId);
        //주문 취소
        order.cancel();
    }
    /**
     * 주문 검색
     */
    public List<Order> findOrders(OrderSearch orderSearch){
        return orderRepository.findAllByString(orderSearch);
    }
}
