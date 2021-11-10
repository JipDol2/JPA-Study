package jpabook1.jpashop.controller;

import jpabook1.jpashop.domain.Address;
import jpabook1.jpashop.domain.Member;
import jpabook1.jpashop.service.MemberService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;

import javax.validation.Valid;
import java.util.List;

@Controller
@RequiredArgsConstructor
public class MemberController {

    private final MemberService memberService;

    @GetMapping("/members/new")
    public String createForm(Model model){
        model.addAttribute("memberForm",new MemberForm());
        return "members/createMemberForm";
    }

    /**
     * URL 경로가 같지만 GET은 그냥 페이지를 여는 용도, POST는 직접 데이터를 넣는 용도이므로 구분이 된다.
     */
    @PostMapping("/members/new")
    public String create(@Valid MemberForm memberForm, BindingResult result){
        //MemberForm 의 name을 필수로 사용하기 위해 @Valid 어노테이션을 사용한다
        //또한, memerForm에 문제가 생기면 원래는 튕기지만 BindingResult로 처리할 수 있다.

        if(result.hasErrors()){
            return "members/createMemberForm";
        }
        Address address = new Address(memberForm.getCity(), memberForm.getStreet(), memberForm.getZipcode());

        Member member = new Member();
        member.setName(memberForm.getName());
        member.setAddress(address);

        memberService.join(member);
        return "redirect:/";    //첫번째 페이지로 넘어간다.
    }

    /**
     * API를 만들때는 절대로 Entity를 반환하면 안된다.
     */
    @GetMapping("/members")
    public String list(Model model){
        List<Member> members = memberService.findMembers();
        model.addAttribute("members",members);
        return "members/memberList";
    }
}
