package jpabook1.jpashop.service;

import jpabook1.jpashop.domain.Address;
import jpabook1.jpashop.domain.Member;
import jpabook1.jpashop.domain.Order;
import jpabook1.jpashop.domain.OrderStatus;
import jpabook1.jpashop.domain.item.Book;
import jpabook1.jpashop.domain.item.Item;
import jpabook1.jpashop.exception.NotEnoughStockException;
import jpabook1.jpashop.repository.OrderRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(SpringExtension.class)
@SpringBootTest
@Transactional
class OrderServiceTest {

    @Autowired
    EntityManager em;
    @Autowired
    OrderService orderService;
    @Autowired
    OrderRepository orderRepository;

    @Test
    public void 상품주문() throws Exception{
        //given
        Member member = createMember();

        Book book = createBook("시골 JPA", 10000, 10);

        int orderCount=2;

        //when
        Long orderId = orderService.order(member.getId(), book.getId(), orderCount);

        //then
        Order getOrder = orderRepository.findOne(orderId);

        //Junit5 에서는 첫 파라미터 : 기대값(expected) , 두번째 : 실제값(actual) , 세번째 : 설명 메세지 이다.
        //Junit4 에서는 설명 메세지가 첫번째로 들어온다.
        assertEquals( OrderStatus.ORDER,getOrder.getStatus(),"상품 주문시 상태는 Order");
        assertEquals( 1,getOrder.getOrderItems().size(),"주문한 상품 종류 수가 정확해야 한다.");
        assertEquals( 10000*orderCount,getOrder.getTotalPrice(),"주문 가격은 가격*수량이다");
        assertEquals( 8,book.getStockQuantity(),"주문 수량만큼 재고가 줄어야 한다.");
    }
    @Test
    public void 상품취소() throws Exception{
        //given
        Member member = createMember();
        Book item = createBook("시골 JPA",10000,10);

        int orderCount=2;
        Long orderId = orderService.order(member.getId(),item.getId(),orderCount);
        //when
        orderService.cancelOrder(orderId);

        //then
        Order getOrder = orderRepository.findOne(orderId);

        assertEquals(OrderStatus.CANCEL,getOrder.getStatus(),"주문 취소시 상태는 CANCEL 이다.");
        assertEquals(10,item.getStockQuantity(),"주문이 취소된 상품은 그만큼 재고가 증가해야 한다.");
    }
    //Junit4 의 expected 는 Junit5 에서 없어지고, assertThrows 를 사용해야 한다.
    @Test
    public void 상품재고수량초과() throws Exception{
        //given
        Member member = createMember();
        Item item = createBook("시골 Jpa", 10000, 10);
        int count = 11;

        //when, then

        //NotEnoughStockException 이 터져야지 테스트가 성공한 것이다. 즉, 예외가 터지지 않는다면 실패다.
        assertThrows(NotEnoughStockException.class,
                () -> orderService.order(member.getId(), item.getId(), count), "재고 수량 부족 예외가 발생해야 한다.");
    }
    private Book createBook(String name, int price, int stockQuantity) {
        Book book = new Book();
        book.setName(name);
        book.setPrice(price);
        book.setStockQuantity(stockQuantity);
        em.persist(book);
        return book;
    }

    private Member createMember() {
        Member member = new Member();
        member.setName("회원1");
        member.setAddress(new Address("서울","강가","123-123"));
        em.persist(member);
        return member;
    }
}