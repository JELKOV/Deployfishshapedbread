package com.bungeobbang.app.view.paymentController;


import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import com.bungeobbang.app.biz.member.MemberDTO;
import com.bungeobbang.app.biz.member.MemberService;
import com.bungeobbang.app.biz.payment.PaymentDTO;
import com.bungeobbang.app.biz.payment.PaymentService;
import com.bungeobbang.app.biz.point.PointDTO;
import com.bungeobbang.app.biz.point.PointService;

import jakarta.servlet.http.HttpSession;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Controller
public class PaymentController {

    // 결제 내역 보기 전체 조회 단건 조회 x
    // loadListPayment.do 요청 보내면 uuid 받아서 조회
    // 이때 반환 값은 uuid만 반환 해줌
    // session에서 userPK 꺼낸뒤 paymentDTO에 담고
    // 반환 받은 uuid 도 paymentDTO에 담아서
    // selectAll 하면
    // 회원이 결제할때 사용한 uuid가 반환됨
    // 이 uuid 배열을 가지고 단건 조회를 반복문으로 돌림

    @Autowired
    private PaymentService paymentService;

    @Autowired
    private MemberService memberService;

    @Autowired
    private PointService pointService;
    /*
    @Value("${channel.key.shinhan}")
    private String channelKeyShinhan;

    @Value("${channel.key.woori}")
    private String channelKeyWoori;

    @Value("${channel.key.virtualAccount}")
    private String channelKeyVirtualAccount;

    @Value("${channel.key.toss}")
    private String channelKeyToss;

    @Value("${imp.init.key}")
    private String impInitKey;
    */


    // 포인트 환전 내역 전체 검색
    @PostMapping(value = "loadListPayment.do")
    public String paymentSelectAll(HttpSession session, PaymentDTO paymentDTO, Model model, PointDTO pointDTO) {
        log.info("[PaymentSelectAll] 시작");

        Integer memberPK = (Integer) session.getAttribute("userPK");

        paymentDTO.setCondition("SELECTALL_PAYMENT");
        paymentDTO.setMemberNum(memberPK);
        log.info("[PaymentSelectAll View에서 전달 받은 값] : {}", paymentDTO);
        List<PaymentDTO> paymentList = paymentService.selectAll(paymentDTO);

        // session에 memberPoint 업데이트 해주기 ----------------------------------------
        pointDTO.setCondition("SELECTONE_MEMBER_POINT");
        pointDTO.setMemberNum(memberPK);
        pointDTO = pointService.selectOne(pointDTO);
        session.setAttribute("userPoint", pointDTO.getTotalMemberPoint());
        //----------------------------------------------------------------------------

        if(paymentList != null && !paymentList.isEmpty()) {
            Integer sessionPoint = (Integer) session.getAttribute("sessionPoint");
            model.addAttribute("sessionPoint", sessionPoint);
            model.addAttribute("paymentList", paymentList); // 모델에 리스트 추가
            log.info("[PaymentSelectAll selectAll 이후에 보내주는 model 값] : {}", model);
        }
        return "moneyToPoint";
    }

    @RequestMapping(value = "/addPoint.do") // 포인트 충전 페이지 이동 controller
    public String addPoint(HttpSession session, Model model, MemberDTO memberDTO, PointDTO pointDTO) {
        Integer memberPK = (Integer) session.getAttribute("userPK");
        
        String channelKeyShinhan = System.getenv("CHANNEL_KEY_SHINHAN");
        String channelKeyWoori = System.getenv("CHANNEL_KEY_WOORI");
        String channelKeyVirtualAccount = System.getenv("CHANNEL_KEY_VIRTUAL_ACCOUNT");
        String channelKeyToss = System.getenv("CHANNEL_KEY_TOSS");
        String impInitKey = System.getenv("IMP_INIT_KEY");
        
        log.info("Shinhan Key: {}", channelKeyShinhan);
        log.info("Woori Key: {}", channelKeyWoori);
        log.info("Virtual Account Key: {}", channelKeyVirtualAccount);
        log.info("Toss Key: {}", channelKeyToss);
        log.info("PortOne Init Key: {}", impInitKey);
        
        
        log.info("[");
        memberDTO.setCondition("INFO_CONDITION");
        memberDTO.setMemberNum(memberPK);
        log.info("[포인트 충전 페이지 이동 전 session에서 가져온 memberPK] : {}", memberPK);
        memberDTO = memberService.selectOne(memberDTO);
        log.info("[포인트 충전 페이지 이동 전 DB에서 가져오는 memberName] : {}", memberDTO);

        // session에 memberPoint 업데이트 해주기 ----------------------------------------
        pointDTO.setCondition("SELECTONE_MEMBER_POINT");
        pointDTO.setMemberNum(memberPK);
        pointDTO = pointService.selectOne(pointDTO);
        session.setAttribute("userPoint", pointDTO.getTotalMemberPoint());
        //----------------------------------------------------------------------------

        model.addAttribute("memberName", memberDTO.getMemberName());
        model.addAttribute("channelKeyShinhan", channelKeyShinhan);
        model.addAttribute("channelKeyWoori", channelKeyWoori);
        model.addAttribute("channelKeyVirtualAccount", channelKeyVirtualAccount);
        model.addAttribute("channelKeyToss", channelKeyToss);
        model.addAttribute("impInitKey", impInitKey);
        return "pointRecharge";
    }

}
