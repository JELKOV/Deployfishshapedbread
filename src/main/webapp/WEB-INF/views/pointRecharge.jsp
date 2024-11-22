<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="custom" tagdir="/WEB-INF/tags"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"%>
<c:set var="path" value="${pageContext.request.contextPath}" />

<!DOCTYPE html>
<html lang="en">

<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>포인트 충전</title>
    <!-- 파비콘 -->
<link rel="icon" href="${path}/resources/assets/images/logo.png"
	type="image/x-icon" />
    
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/css/bootstrap.min.css" rel="stylesheet">
    <link rel="stylesheet" href="${path}/resources/assets/css/main.css" />    
    <link rel="stylesheet" href="${path}/resources/assets/css/point/pointRecharge.css" />
    <link href="https://fonts.googleapis.com/css2?family=Noto+Sans+KR:wght@400;500;700&display=swap" rel="stylesheet">
    <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.0.0-beta3/css/all.min.css">
    <script src="https://cdn.iamport.kr/v1/iamport.js"></script>
    <script>
	    // 서버에서 전달된 키 값들을 JS 변수로 설정
	    const channelKeyShinhan = '${channelKeyShinhan}';
	    const channelKeyWoori = '${channelKeyWoori}';
	    const channelKeyVirtualAccount = '${channelKeyVirtualAccount}';
	    const channelKeyToss = '${channelKeyToss}';
	
	    const impInitKey = '${impInitKey}';
	</script>
</head>

<body>
<!-- Header -->
 <div id="page-wrapper">
<custom:header />


    <div class="container">
        <div class="row mb-4">
            <div class="col-10">
                <h2>포인트 충전</h2>
            </div>
        </div>

        <div class="row mb-4 table-aligned">
            <div class="col-md-12">
                <h3>충전 포인트 선택 <i class="fas fa-coins"></i></h3> 
                <div class="point-options">
                    <div><input type="radio" name="point" id="1000point 구매" value="1000"> <span>1,000 Point</span> <span>1,000원</span></div>
                    <div><input type="radio" name="point" id="3000point 구매" value="3000"> <span>3,000 Point</span> <span>3,000원</span></div>
                    <div><input type="radio" name="point" id="5000point 구매" value="5000"> <span>5,000 Point</span> <span>5,000원</span></div>
                    <div><input type="radio" name="point" id="10000point 구매" value="10000"> <span>10,000 Point</span> <span>10,000원</span></div>
                    <div><input type="radio" name="point" id="30000point 구매" value="30000"> <span>30,000 Point</span> <span>30,000원</span></div>
                    <div><input type="radio" name="point" id="50000point 구매" value="50000"> <span>50,000 Point</span> <span>50,000원</span></div>
                </div>
            </div>
        </div>

        <div class="row mb-4 table-aligned">
            <div class="col-md-12 text-right">
                <div class="current-point">
                    <p>현재 포인트: <span>${userPoint}P</span></p>
                    <p>충전 후 포인트: <span id="totalPoints">${userPoint}P</span></p>
                </div>
            </div>
        </div>

		<div class="row mb-3">
		    <!-- 카드 결제 -->
		    <div class="col-md-4">
		        <div class="form-check">
		            <label class="form-check-label" for="cardPayment">
		                <i class="fas fa-credit-card"></i> 카드 결제
		            </label>
		            <select class="form-select dropdown-payment" id="cardPayment" onchange="selectPaymentMethod(this.value)">
		                <option value="" disabled selected>카드사 선택</option>
		                <option value="신한카드">신한카드</option>
		                <option value="우리카드">우리카드</option>
		            </select>
		        </div>
		    </div>
		
		    <!-- 계좌 결제 -->
		    <div class="col-md-4">
		        <div class="form-check">
		            <label class="form-check-label" for="accountPayment">
		                <i class="fas fa-university"></i> 계좌 결제
		            </label>
		            <select class="form-select dropdown-payment" id="accountPayment" onchange="selectPaymentMethod(this.value)">
		                <option value="" disabled selected>가상계좌</option>
		                <option value="가상계좌">가상계좌</option>
		            </select>
		        </div>
		    </div>
		
		    <!-- 간편 결제 -->
		    <div class="col-md-4">
		        <div class="form-check">
		            <label class="form-check-label" for="otherPayment">
		                <i class="fas fa-running"></i> 간편 결제
		            </label>
		            <select class="form-select dropdown-payment" id="otherPayment" onchange="selectPaymentMethod(this.value)">
		                <option value="" disabled selected>기타 선택</option>
		                <option value="카카오페이">카카오페이</option>
		                <option value="토스페이">토스페이</option>
		            </select>
		        </div>
		    </div>
		</div>

        <div class="row mb-4 table-aligned">
            <div class="col-md-12">
                <h3>결제 정보</h3>
                <div class="mb-3">
                    <label for="email">이메일</label>
                    <input type="email" id="email" class="form-control" placeholder="이메일을 입력하세요">
                </div>
                <div class="mb-3">
                    <label for="paymentMethodSelected">결제 방법</label>
                    <input type="text" id="paymentMethodSelected" class="form-control" value="" readonly>
                </div>
                <div class="mb-3">
                    <label for="paymentAmount">결제 금액</label>
                    <input type="text" id="paymentAmount" class="form-control" value="0원" readonly>
                </div>
                <div class="form-check">
				    <input class="form-check-input" type="checkbox" id="agreeTerms">
				    <label class="form-check-label" for="agreeTerms">
                        위 내용으로 결제에 동의합니다.
                    </label>
                </div>
				<div class="final-buttons mt-3">
				    <button class="btn btn-primary" id="payButton" onclick="checkAgreement()">결제하기</button>
				</div>
            </div>
        </div>
    </div>

<!-- Footer -->
<custom:footer />
   </div>


    <script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/js/bootstrap.bundle.min.js"></script>
    <!-- 현재포인트 전역 변수 설정 -->
    <script>
    	window.currentPoints = ${userPoint};
    </script>
    <script src="${path}/resources/assets/js/point/pointRecharge.js"></script>
    <script src="${path}/resources/assets/js/point/portOne.js"></script>
    <!-- 동의 여부 관련해서 결제 오픈 체크 -->
	<script src="https://cdn.jsdelivr.net/npm/sweetalert2@11"></script>
	<script>

        // 페이지가 로드된 후 JavaScript 함수 호출 / 포인트 실시간 변동 보여주기
        document.addEventListener('DOMContentLoaded', function() {
            var currentUserPoint = ${userPoint}; // JSP EL을 사용해 세션 값 전달
            updateTotalPoint(currentUserPoint); // 외부 JS 함수 호출
        });

        // 결제버튼 이전 유효성 검사
        function checkAgreement() {
            const agreeTerms = document.getElementById('agreeTerms');
            const paymentMethodSelected = document.getElementById('paymentMethodSelected').value;
            const paymentAmount = document.getElementById('paymentAmount').value;

            if (!agreeTerms.checked) {
                // 동의 체크 필요시 메시지 표시
                Swal.fire({
                    icon: 'warning',
                    title: '결제 동의 필요',
                    text: '결제에 동의하셔야 진행할 수 있습니다.',
                    confirmButtonText: '확인'
                });
                return;
            }

            if (!paymentMethodSelected) {
                // 결제 방법 선택 필요 메시지 표시
                Swal.fire({
                    icon: 'warning',
                    title: '결제 방법 선택 필요',
                    text: '결제 방법을 선택하셔야 합니다.',
                    confirmButtonText: '확인'
                });
                return;
            }

            if (paymentAmount === "0원" || paymentAmount === "") {
                // 결제 금액 필요 메시지 표시
                Swal.fire({
                    icon: 'warning',
                    title: '결제 금액 필요',
                    text: '충전할 포인트 금액을 선택해주세요.',
                    confirmButtonText: '확인'
                });
                return;
            }

            // 모든 조건이 충족되었을 때만 결제 함수 호출
            requestPay();
        }
            var memberName="${memberName != null ? memberName : ''}";  // JSP 서버에서 전달된 값
</script>
</body>

</html>
