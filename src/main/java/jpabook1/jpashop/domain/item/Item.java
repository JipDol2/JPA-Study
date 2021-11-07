package jpabook1.jpashop.domain.item;

import jpabook1.jpashop.domain.Category;
import jpabook1.jpashop.exception.NotEnoughStockException;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
@DiscriminatorColumn(name="dtype")
@Getter @Setter
public abstract class Item {

    @Id
    @GeneratedValue
    @Column(name="item_id")
    private Long id;

    private String name;
    private int price;
    private int stockQuantity;

    /*
        다대다 관계는 1:N , N:1 관계로 찢는게 좋다.
    */
    @ManyToMany(mappedBy = "items")
    private List<Category> categories = new ArrayList<>();

    /**
     * stock 증가
     */
    public void addStock(int quantity){
        this.stockQuantity+=quantity;
    }
    /**
     * stock 감소
     */
    public void removeStock(int quantity){
        int restStock = this.stockQuantity-quantity;
        if(restStock<0){
            throw new NotEnoughStockException("needs more stock");
        }
        this.stockQuantity=restStock;
    }
}
