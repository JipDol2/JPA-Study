package jpabook1.jpashop.domain;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import javax.validation.constraints.NotEmpty;
import java.util.ArrayList;
import java.util.List;

@Entity
@Getter @Setter
public class Member {

    @Id @GeneratedValue
    @Column(name="member_id")
    private Long id;

    @NotEmpty
    private String name;

    //Embedded type 은 사용자가 직접 정의한 값 타입이다. 여기서 Embedded type을 사용하지 않으면,집 주소에 관한 정보
    //city,street,zipcode 를 전부 정의해 줘야 되는데 그러면 객체지향적이지 않고 응집력을 떨어뜨리는 원인이 된다.
    //때문에 Embedded type을 사용하여 코드를 좀 더 명확히 한 것이다.
    @Embedded
    private Address address;

    @OneToMany(mappedBy = "member")
    private List<Order> orders = new ArrayList<>();
}
