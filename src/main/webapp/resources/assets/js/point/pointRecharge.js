// 결제 방법 선택 함수

function selectPaymentMethod(method) {
    let pg = '';
    let pay_method = '';
    let channelKey='';
    let buyer_name = '';
    // 카드사 선택에 따른 PG사 및 결제 방식 설정
    if (method === '신한카드' || method === '우리카드') {
        pg = 'tosspay';
        pay_method = 'card';
        channelKey = channelKeyShinhan;
        window.selectedCardCompany = method;
    }
    else if (method === '가상계좌') {
        pg = 'html5_inicis';
        pay_method = 'vbank';
        channelKey = channelKeyVirtualAccount;
        buyer_name = memberName;
        window.selectedCardCompany = method;
    }
    else if (method === '카카오페이') {
        pg = 'kakaopay';
        pay_method = 'card';
    }
    else if (method === '토스페이') {
        pg = 'tosspay'; // 토스페이 간편결제
        pay_method = 'tosspay';
        channelKey = channelKeyToss;

    }

    // 선택된 결제 방식과 PG사 값을 전역 변수로 설정
    window.selectedPG = pg;
    window.selectedPM = pay_method;
    window.selectedCK = channelKey;
    window.selectedNAME= buyer_name;
    // 사용자가 선택한 결제 방법을 표시
    document.getElementById('paymentMethodSelected').value = method;
}


// 포인트 선택 시 결제 금액과 총 포인트 업데이트 함수
function updatePaymentAmountAndTotalPoints() {
    // 선택된 포인트 라디오 버튼의 값을 가져옴
    const selectedPoint = document.querySelector('input[name="point"]:checked');
    const additionalPoints = selectedPoint ? parseInt(selectedPoint.value) : 0;
    // portOne 결제를 하기 위해 전역변수 설정
    window.additionalPoints = additionalPoints;
    // 결제 금액 업데이트
    document.getElementById('paymentAmount').value = `${additionalPoints.toLocaleString()}원`;

    // 충전 후 총 포인트 계산
    const totalPoints = window.currentPoints + additionalPoints;
    document.getElementById('totalPoints').textContent = `${totalPoints.toLocaleString()}P`;
}

// 초기화 작업: 드롭다운 결제 방식 선택 시 다른 드롭다운 초기화
function initializeDropdowns() {
    document.querySelectorAll('.dropdown-payment').forEach(select => {
        select.addEventListener('change', function () {
            document.querySelectorAll('.dropdown-payment').forEach(otherSelect => {
                if (otherSelect !== this) {
                    otherSelect.selectedIndex = 0; // 다른 드롭다운 선택 초기화
                }
            });
            selectPaymentMethod(this.value); // 선택한 결제 방식을 업데이트
        });
    });
}

// 페이지 로드 후 이벤트 리스너 추가
document.addEventListener('DOMContentLoaded', function () {
    // 각 포인트 옵션에 이벤트 리스너 추가하여 변경 시 업데이트
    document.querySelectorAll('input[name="point"]').forEach(function (radio) {
        radio.addEventListener('change', updatePaymentAmountAndTotalPoints);
    });

    // 드롭다운 초기화 함수 호출
    initializeDropdowns();
});
