package jpabook1.jpashop.repository;

import jpabook1.jpashop.domain.item.Item;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import java.util.List;

@Repository
@RequiredArgsConstructor
public class ItemRepository {
    private final EntityManager em;

    public void save(Item item){
        if(item.getId()==null){
            em.persist(item);
        }else{
            /**
             * 보통 merger를 사용하지 않는다. merge는 모든 데이터들을 변경해버린다.
             * 만약 가격은 변동 불가인데 기존에 따로 가격을 set 해주지 않았기 때문에 null이 들어가 버린다.
             * 이는 굉장히 위험하다.
             * 따라서 그냥 '변경감지'를 사용하자.
             * JPA책 115 page 참조
             */
            em.merge(item);
        }
    }

    public Item findOne(Long id){
        return em.find(Item.class,id);
    }

    public List<Item> findAll(){
        return em.createQuery("select i from Item i", Item.class)
                .getResultList();
    }
}
