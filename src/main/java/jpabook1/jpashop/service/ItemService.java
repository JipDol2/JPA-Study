package jpabook1.jpashop.service;

import jpabook1.jpashop.domain.item.Item;
import jpabook1.jpashop.repository.ItemRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class ItemService {

    private final ItemRepository itemRepository;

    @Transactional
    public void saveItem(Item item){
        itemRepository.save(item);
    }
    @Transactional
    public void updateItem(Long itemId,String name,int price, int stockQuantity){
        Item item = itemRepository.findOne(itemId);
        /**
         * setter를 되도록이면 쓰지 말자.
         * item.change(name,price,stockQuantity);
         * 이렇게 메소드를 만드는 게 훨씬 낫다.
         */
        item.setName(name);
        item.setPrice(price);
        item.setStockQuantity(stockQuantity);
    }

    public Item findOne(Long id){
        return itemRepository.findOne(id);
    }

    public List<Item> findItems(){
        return itemRepository.findAll();
    }
}
