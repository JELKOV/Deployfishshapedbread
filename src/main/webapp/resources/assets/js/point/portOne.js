var IMP = window.IMP;
IMP.init(impInitKey);

function requestPay() {
    // 사전 결제 보내는 값
    const uuid = crypto.randomUUID().replace(/-/g, "");
    const currentDate = new Date().toISOString().slice(0, 10).replace(/-/g, '');
    const merchant_uid = uuid + currentDate; // 주문 번호 생성
    let amount = window.additionalPoints; // 선택한 포인트 금액을 결제 금액으로 설정

    // PG사와 결제 방식 설정
    const pg = window.selectedPG;
    const pay_method = window.selectedPM;
    const cardCompany = window.selectedCardCompany;
    const channelKey = window.selectedCK;
    const buyer_name= window.selectedNAME;
    // 콘솔에 주요 변수 출력
    console.log("사용자 이름:", buyer_name);
    console.log("채널키:",channelKey);
    console.log("UUID:", uuid);
    console.log("현재 날짜:", currentDate);
    console.log("주문 번호:", merchant_uid);
    console.log("금액:", amount);
    console.log("PG:", pg);
    console.log("결제 방법:", pay_method);
    console.log("카드 회사:", cardCompany);

    var selectedRadio = document.querySelector('input[name="point"]:checked');
    const product_name = selectedRadio ? selectedRadio.id : null;

    // 결제 방법 및 사용자 정보 가져오기
    const registered_email = document.getElementById('email').value || null;

    // 추가 콘솔 출력
    console.log("선택된 상품 이름:", product_name);
    console.log("등록된 이메일:", registered_email);

    // 사전 검증 등록
    fetch("addPrepare.do", {
        method: "POST",
        headers: {
            "Content-Type": "application/x-www-form-urlencoded"
        },
        body: `merchant_uid=${merchant_uid}&amount=${amount}`
    })
        .then(rsp => {
            if (!rsp.ok) throw new Error('네트워크 응답이 올바르지 않습니다');
            return rsp.text();
        })
        .then(data => {
            console.log("[사전검증 등록 이후 반환 값]:", data);
            if (data === 'true') {
                console.log("사전 검증 등록 완료");

                // 사전 검증 조회
                return fetch("checkPrepareResult.do", {
                    method: "POST",
                    headers: {
                        "Content-Type": "application/x-www-form-urlencoded",
                    },
                    body: `merchant_uid=${merchant_uid}&amount=${amount}`
                });
            }
            else {
                console.error("사전 검증 등록 실패");
                throw new Error("검증 등록 실패");
            }
        })
        .then(response => {
            console.log("사전검증 조회 이후 반환 받은 response :", response.status);
            if (!response.ok) {
                throw new Error('네트워크 응답이 올바르지 않습니다');
            }
            return response.json();
        })
        .then(data => {
            console.log("사전검증 조회 이후 반환 받은 데이터: ", data);
            console.log("data의 타입: ", typeof data);

            // data가 문자열이면 JSON으로 파싱
            if (typeof data === 'string') {
                data = JSON.parse(data); // 문자열을 JSON 객체로 변환
                console.log("파싱이후 data : ", data);
            }

            // DTO 구조에 맞춰서 데이터를 처리
            const { merchant_uid, amount } = data; // JSON에서 값 추출

            console.log("사전검증에 등록된 uuid :", merchant_uid);
            console.log("사전검증에 등록된 상품 금액 :", amount);

            if (!isNaN(amount) && amount > 0) {
                console.log("사전 검증 조회 성공, 금액:", amount);
            }
            else {
                console.error("오류 발생: 반환된 값이 유효하지 않음");
            }
        })
        .catch(error => {
            console.error("Fetch 오류:", error);
        });

    // 결제 요청
    IMP.request_pay({
        channelKey,
        pg,
        buyer_name,
        pay_method,
        cardCompany,
        merchant_uid,
        amount,
        name: product_name,
        buyer_email: registered_email
    }, function (rsp) {
        console.log("결제 응답:", rsp);
        if (rsp.success) {
            // 결제 성공 시 단건 조회
            fetch("infoPayment.do", {
                method: "POST",
                headers: {
                    "Content-Type": "application/x-www-form-urlencoded"
                },
                body: `imp_uid=${rsp.imp_uid}&merchant_uid=${rsp.merchant_uid}`
            })
                .then(response => {
                    if (!response.ok) throw new Error('네트워크 응답이 올바르지 않습니다');
                    return response.json(); // JSON으로 응답을 파싱
                })
                .then(data => {
                    console.log("결제 정보 응답:", data);

                    if (typeof data === 'string') {
                        data = JSON.parse(data); // 문자열을 JSON 객체로 변환
                    }

                    const { result, userPoint } = data; // JSON에서 값 추출
                    console.log("결제 끝난 뒤 data 값에 있는 result 값 : ", result);
                    console.log("결제 끝난 뒤 data 값에 있는 userPoint 값 : ", userPoint);

                    // 서버 응답의 result가 true일 때 처리
                    if (result === true) {
                        // userPoint 값을 sessionStorage에 저장
                        location.href = `addPoint.do`;
                    }
                    else {
                        alert("검증 실패");
                        location.href = `cancelPayment.do?merchant_uid=${data.merchant_uid}`;
                    }
                })
                .catch(error => {
                    console.error("결제 검증 Fetch 오류:", error);
                });
        }
        else {
            console.error("결제 실패: ", rsp.error_msg);
            alert("결제에 실패하였습니다. 에러 내용: " + rsp.error_msg);
        }
    });
}
