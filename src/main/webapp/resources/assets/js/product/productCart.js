// 초기 데이터를 cartItems로 설정
var productList = cartItems;
// memberPK 값을 JavaScript로 가져오기
const memberNum = document.getElementById("memberPK") ? document.getElementById("memberPK").textContent : null;

// SweetAlert 알림 함수
function showAlert(icon, title, text, confirmButtonText = '확인') {
    return Swal.fire({
        icon,
        title,
        text,
        confirmButtonText
    });
}

$(document).ready(function() {
    renderCart(false); // 처음에는 체크박스를 활성화된 상태로 렌더링

    // 전체 선택 체크박스 이벤트 리스너 설정
    $('#selectAll').on('change', function() {
        const isChecked = $(this).is(':checked');
        
        // 모든 제품의 isChecked 상태 업데이트
        productList.forEach(product => product.isChecked = isChecked);

        $('.product-checkbox').prop('checked', isChecked);
        
        if (isChecked) {
            $('.remove-btn').show(); // 모든 삭제 버튼 표시
            $('#deleteAllBtn').show(); // 전체 삭제 버튼 표시
            $('#selectAll').next('label').text('전체 해제'); // 라벨 변경
        } else {
            $('.remove-btn').hide(); // 모든 삭제 버튼 숨기기
            $('#deleteAllBtn').hide(); // 전체 삭제 버튼 숨기기
            $('#selectAll').next('label').text('전체 선택'); // 라벨 변경
        }

        updateTotalPrice(); // 총 금액 업데이트
    });

    // 개별 체크박스 변경 시 전체 선택 체크박스 상태 업데이트
    $(document).on('change', '.product-checkbox', function() {
        const productId = $(this).closest('.product-item').find('.remove-btn').data('product-id');
        const isChecked = $(this).is(':checked');
        
        // 해당 제품의 isChecked 상태 업데이트
        const product = productList.find(p => p.productNum === productId);
        if (product) {
            product.isChecked = isChecked;
        }

        const totalCheckboxes = $('.product-checkbox').length;
        const checkedCheckboxes = $('.product-checkbox:checked').length;

		// 전체 선택 체크박스 상태를 결정
		if (checkedCheckboxes === totalCheckboxes) {
		    $('#selectAll').prop('checked', true);
		    $('#selectAll').next('label').text('전체 해제'); // 선택된 상태 표시
		} else {
		    $('#selectAll').prop('checked', false);
		    $('#selectAll').next('label').text('전체 선택'); // 일부 또는 0개 선택된 상태 표시
		}

        // 전체 삭제 버튼 표시 여부 결정
        if (checkedCheckboxes > 0) {
            $('#deleteAllBtn').show();
        } else {
            $('#deleteAllBtn').hide();
        }

        // 개별 선택 시 개별 삭제 버튼 토글
        $(this).closest('.product-item').find('.remove-btn').toggle(isChecked);
        updateTotalPrice();
    });

    // 개별 삭제 버튼 클릭 시 해당 제품 삭제
    $(document).on('click', '.remove-btn', function(event) {
        event.stopPropagation();
        const productId = $(this).data('product-id');
        removeProduct(productId);
    });
    
    // 전체 삭제 버튼 클릭 이벤트 설정
    $('#deleteAllBtn').on('click', function() {
        removeAllProducts();
        resetSelectionButtons();
    });
});

// 삭제 후 전체 선택 버튼과 전체 삭제 버튼 초기화를 위한 함수
function resetSelectionButtons() {
    $('#selectAll').prop('checked', false);
    $('#selectAll').next('label').text('전체 선택');
    $('#deleteAllBtn').hide();
}

// 선택된 상태를 유지하며 장바구니 렌더링
function renderCart(disableSelectedXButtons = false) {
    const cartProductsContainer = document.querySelector('.cart-products');
    cartProductsContainer.innerHTML = ''; // 기존 내용을 지웁니다.

    productList.forEach((product) => {
        const productItem = document.createElement('div');
        productItem.className = 'product-item';
        productItem.innerHTML = `
            <div class="product-info">
                <input type="checkbox" class="form-check-input me-2 product-checkbox" 
                    ${product.isChecked ? 'checked' : ''} ${disableSelectedXButtons && product.isChecked ? 'disabled' : ''}>
                <div class="product-image">
                    <img src="${product.productProfileWay}" alt="${product.productName}" class="img-fluid">
                </div>
                <div>
                    <strong>${product.productName}</strong><br>
                    ${product.productPrice}P / 
                    <span class="product-quantity">
                        <select class="form-select form-select-sm" data-product-id="${product.productNum}">
                            ${[...Array(10).keys()].map(i => `
                                <option value="${i + 1}" ${product.quantity === i + 1 ? 'selected' : ''}>
                                    ${i + 1}개
                                </option>`).join('')}
                        </select>
                    </span>
                </div>
            </div>
            <button class="btn btn-light remove-btn" data-product-id="${product.productNum}" 
                ${disableSelectedXButtons && product.isChecked ? 'disabled' : ''} 
                style="display: ${product.isChecked ? 'inline-block' : 'none'};">X</button>
        `;
        cartProductsContainer.appendChild(productItem);
    });

    updateTotalPrice(); // 장바구니가 렌더링된 후 총 금액을 업데이트합니다.
    attachEventListeners(); // 삭제 버튼 이벤트 리스너를 다시 연결합니다.
    setupChargeButton();
    setupPurchaseButton();
}



//  상품 수량 변경 시 이벤트 리스너를 추가하여 상품의 수량 변경과 총 가격 업데이트
function attachEventListeners() {
    const quantitySelectors = document.querySelectorAll('.product-quantity select');
    quantitySelectors.forEach((select) => {
        select.addEventListener('change', function() {
            const productId = parseInt(this.getAttribute('data-product-id'));
            const selectedQuantity = parseInt(this.value);
            updateProductQuantity(productId, selectedQuantity);
            updateTotalPrice();
        });
    });
}


// 개별 제품을 장바구니에서 삭제하는 함수
async function removeProduct(productId) {
    // 주어진 productId에 해당하는 제품의 인덱스를 찾음
    const productIndex = productList.findIndex(product => product.productNum === productId);

    // 제품이 productList에 존재하는지 확인
    if (productIndex !== -1) {
        // 화면에서 해당 제품 요소를 찾아 페이드 아웃 애니메이션을 추가하여 삭제 효과
        const productItem = document.querySelector(`.remove-btn[data-product-id="${productId}"]`).closest('.product-item');
        productItem.classList.add('fade-out');

        try {
            // 서버에 제품 삭제 요청을 비동기로 보냄
            const response = await fetch('removeCart.do', {
                method: 'POST', // HTTP POST 요청
                headers: {
                    'Content-Type': 'application/json' // 요청 본문이 JSON 형식임을 명시
                },
                body: JSON.stringify({ productNum: productId }) // JSON 데이터로 productId 전송
            });

            // 요청이 성공적으로 완료된 경우
            if (response.ok) {
                const result = await response.json(); // JSON 형식의 응답을 파싱

                // 서버 응답에서 삭제 성공 여부를 확인
                if (result.status === "success") {
                    console.log('서버에서 제품이 성공적으로 삭제되었습니다.');
                    productList.splice(productIndex, 1); // productList 배열에서 해당 제품 제거
                    productItem.remove(); // 화면에서 해당 제품 요소 삭제
					// 선택된 제품 수를 확인하여 `deleteAllBtn`의 표시 여부를 결정합니다.
	                const checkedCheckboxes = $('.product-checkbox:checked').length;
	                if (checkedCheckboxes > 0) {
	                    $('#deleteAllBtn').show();
	                } else {
	                    $('#deleteAllBtn').hide();
	                }
	
	                // 남은 모든 체크박스가 선택되었는지 여부에 따라 `#selectAll` 상태를 업데이트합니다.
	                const totalCheckboxes = $('.product-checkbox').length;
	                $('#selectAll').prop('checked', totalCheckboxes === checkedCheckboxes);
					
                    renderCart(false); // 장바구니를 다시 렌더링하여 최신 상태로 갱신
                } else {
                    console.error('서버에서 제품 삭제 실패:', result.message); // 실패 메시지 출력
                    Swal.fire({
                        icon: 'error',
                        title: '삭제 실패',
                        text: result.message || '삭제 중 문제가 발생했습니다. 다시 시도해주세요.',
                        confirmButtonText: '확인'
                    });
                }
            } else {
                console.error('서버 응답 오류:', response.statusText); // 서버 응답 오류 처리
                Swal.fire({
                    icon: 'error',
                    title: '삭제 요청 실패',
                    text: '삭제 요청에 실패했습니다. 네트워크 상태를 확인해주세요.',
                    confirmButtonText: '확인'
                });
            }
        } catch (error) {
            // 네트워크 또는 요청 처리 중 발생한 오류를 처리
            console.error('AJAX 요청 오류:', error);
            Swal.fire({
                icon: 'error',
                title: '삭제 요청 실패',
                text: '삭제 요청에 실패했습니다. 네트워크 상태를 확인해주세요.',
                confirmButtonText: '확인'
            });
        }
    }
}

// 선택된 모든 제품을 장바구니에서 삭제하는 함수
async function removeAllProducts() {
    // 선택된 제품 ID를 가져옴
    const productsToDelete = $('.product-checkbox:checked').map(function() {
        return $(this).closest('.product-item').find('.remove-btn').data('product-id');
    }).get();

    // 선택된 각 제품을 삭제하기 위해 removeProduct 함수를 호출
    for (let productId of productsToDelete) {
        await removeProduct(productId); // 개별 제품 삭제 후 다음 제품으로 진행
    }
	// 모든 제품이 삭제된 후 전체 선택 상태를 해제
	$('#selectAll').prop('checked', false);
	$('#selectAll').next('label').text('전체 선택');
}

// 제품 수량 업데이트 함수
function updateProductQuantity(productId, quantity) {
    const product = productList.find(product => product.productNum === productId);
    if (product) {
        product.quantity = quantity;
    }
}

// 총 금액 업데이트 함수 (선택된 값만 가격으로 표시)
function updateTotalPrice() {
    const totalPriceElement = document.querySelector('.total-price');
    let totalPrice = 0;

    console.log('--- 총 금액 업데이트 시작 ---');
    // 선택된 체크박스에 해당하는 제품만 가격을 합산
    $('.product-checkbox:checked').each(function() {
        // 체크박스의 가장 가까운 부모 요소인 .product-item에서 productId를 찾습니다.
        const productId = $(this).closest('.product-item').find('.remove-btn').data('product-id');
        console.log('선택된 제품 ID:', productId);

        const product = productList.find(p => p.productNum === productId);

        if (product) {
            console.log('선택된 제품 정보:', product);
            console.log('제품 가격:', product.productPrice);
            console.log('제품 수량:', product.quantity);
            totalPrice += product.productPrice * product.quantity;
            console.log('현재 누적 총 금액:', totalPrice);
        } else {
            console.log('제품을 찾을 수 없습니다:', productId);
        }
    });

    console.log('최종 총 금액:', totalPrice);
    totalPriceElement.textContent = `${totalPrice.toLocaleString()}P`;
    console.log('--- 총 금액 업데이트 완료 ---');
}

// "충전관련 함수"

function setupChargeButton() {
    // "충전하기" 버튼 클릭 이벤트 리스너
    $('#chargeButton').on('click', function() {
        redirectToChargePage();
    });
}
// 충전 페이지로 리다이렉트하는 함수
function redirectToChargePage() {
    window.location.href = 'addPoint.do';
}

// 선택된 제품 정보를 수집하는 함수
function getSelectedProducts() {
    const selectedProducts = { products: [] };

    $('.product-checkbox:checked').each(function() {
        const productId = $(this).closest('.product-item').find('.remove-btn').data('product-id');
        const product = productList.find(p => p.productNum === productId);

        if (product) {
            selectedProducts.products.push({
                productNum: product.productNum,
				productName: product.productName,
				productPrice: product.productPrice,
                quantity: product.quantity
            });
        }
    });

    console.log('Selected products:', selectedProducts); // 추가하여 데이터 확인
    return selectedProducts;
}


// "구매하기 버튼 관련 함수"
function setupPurchaseButton() {
    // "구매하기" 버튼 클릭 이벤트 리스너
    $('#purchaseButton').on('click', function() {
        // 로그인 여부 확인 
        const isLoggedIn = Boolean(document.getElementById("memberPK"));
        console.log(isLoggedIn);

        // 로그인 여부 확인
        if (!isLoggedIn) {
            showAlert('warning', '로그인 필요', '구매를 진행하려면 로그인이 필요합니다. 로그인 페이지로 이동합니다.', '로그인하기').then(() => {
                // 로그인 페이지로 이동
                window.location.href = 'login.do';
            });
            return;
        }

        const selectedProducts = getSelectedProducts();
        console.log(selectedProducts);

        // 선택된 제품이 있는지 확인
        if (selectedProducts.length === 0) {
            showAlert('warning', '제품 선택', '선택된 제품이 없습니다. 제품을 선택해주세요.');
            return;
        }

        // 총 결제 금액 계산
        let totalPrice = 0;
        selectedProducts.products.forEach(product => {
            const cartProduct = cartItems.find(item => item.productNum === product.productNum);
            if (cartProduct) {
                totalPrice += cartProduct.productPrice * product.quantity;
            }
        });

        // 현재 포인트 가져오기
        const currentPoints = parseInt(document.getElementById('myPoint').innerText.replace('P', ''), 10);
        console.log('현재 포인트:', currentPoints);

        // 결제 금액이 현재 포인트보다 높은지 확인
        if (totalPrice > currentPoints) {
            showAlert('warning', '포인트 부족', '결제 금액이 현재 포인트보다 높습니다. 충전 페이지로 이동합니다.', '충전하기').then(() => {
                // 충전 페이지로 이동
                window.location.href = 'addPoint.do';
            });
        } else {
            // 서버에 구매 요청 전송
            sendPurchaseRequest(selectedProducts);
        }
    });
}
	
function sendPurchaseRequest(selectedProducts) {
    console.log('selectedProducts 데이터:', selectedProducts);
    const selectedProductData = selectedProducts.products;

    // 선택된 상품 데이터를 확인하기 위한 로그 출력
    console.log('확인을 위한 선택된 항목 표시:', selectedProductData);

    // 선택된 상품만 주문 섹션에 표시
    renderSelectedItems(selectedProductData);

	$('#purchaseButton').hide(); // 배송주문하기 버튼 숨기기
    // 배송지 입력 섹션을 보여줌
	$('#orderSection').css("margin-top", "5px").slideDown();
	$('html, body').animate({
	    scrollTop: $('#orderSection').offset().top
	}, 200);
    
	// 장바구니에서 선택 변경을 막음
	$('.product-quantity select').prop('disabled', true); // 수량 선택 비활성화
    $('.product-checkbox').prop('disabled', true); // 모든 체크박스 비활성화
    $('#selectAll').prop('disabled', true);  // 전체 선택 체크박스 비활성화
    $('#deleteAllBtn').prop('disabled', true); // 선택된 상품 삭제 버튼 비활성화
    $('.remove-btn').each(function() {
       $(this).prop('disabled', true); // 삭제 버튼 비활성화
    });

	// 주문 완료 버튼 표시
	$('#completeOrderButton').show();
}


// 선택된 항목을 주문 섹션에 표시하는 함수
function renderSelectedItems(items) {
    $('.order-products').empty(); // 기존 아이템 목록 초기화
    let totalPrice = 0; // 총 가격을 저장할 변수 초기화

    items.forEach(product => {
        const itemTotalPrice = product.productPrice * product.quantity; // 각 상품의 총 가격 계산
        totalPrice += itemTotalPrice; // 전체 총 가격에 더함

        $('.order-products').append(`
            <div class="order-product-item d-flex flex-column justify-content-between align-items-start mb-3 p-3 shadow-sm">
                <div>
                    <span class="product-name">${product.productName}</span>
                </div>
                <div>
                    <span class="product-quantity text-muted">${product.productPrice.toLocaleString()}P x ${product.quantity}개</span><br>
                    <span class="product-price">총: ${itemTotalPrice.toLocaleString()}P</span>
                </div>
            </div>
        `);
    });

    // 총 가격을 표시
    $('#orderTotalPrice').text(`${totalPrice.toLocaleString()}P`);
}


// 주문 완료 버튼 클릭 시 구매 요청 전송
$('#completeOrderButton').on('click', function() {
	const name = $('#nameInput').val();
    const addressMain = $('#addressMain').val();
    const addressDetail = $('#addressDetail').val();
    const phone = $('#phoneInput').val();
    
    // 주소와 전화번호 확인
    if (!addressMain || !addressDetail || !phone) {
        showAlert('warning', '입력 필요', '배송지와 전화번호를 입력해주세요.');
        return;
    }

	// 주문 데이터 구성
	const selectedItems = getSelectedProducts();	
	console.log("Selected Items:", selectedItems);
	const orderData = {
		orderAddress: `${addressMain} ${addressDetail}`,
		memberNum: memberNum,
		memberName: name,
	    phone: phone,
	    orderDetails: selectedItems.products.map(item => ({
	        productNum: item.productNum,
	        productName: item.productName,
	        productPrice: item.productPrice,
	        orderQuantity: item.quantity
	    }))
	};

    // 서버에 주문 데이터 전송
    $.ajax({
        url: 'addOrder.do',
        type: 'POST',
        contentType: 'application/json',
        data: JSON.stringify(orderData),
        success: function(response) {
            showAlert('success', '주문 완료', '주문이 성공적으로 완료되었습니다.').then(() => {
                $('#deleteAllBtn').click();
                window.location.href = 'loadListProduct.do'; // 상품 목록 페이지로 이동
            });
        },
        error: function(xhr, status, error) {
            showAlert('error', '주문 실패', '주문 처리 중 문제가 발생했습니다.');
        }
    });
});



/*
function sendPurchaseRequest(selectedProducts, memberNum) {
    const jsonData = JSON.stringify({
        memberNum: memberNum,
        products: selectedProducts.products
    });
    
    console.log('sendPurchaseRequest: Sending purchase request with data:', jsonData);
    
    $.ajax({
        url: 'purchaseCart.do',
        type: 'POST',
        contentType: 'application/json',
        data: jsonData,
        success: function(response) {
            console.log('sendPurchaseRequest: Response received:', response);
            if (response.status === 'success') {
                showAlert('success', '구매 성공', response.message).then(() => {
                    console.log('sendPurchaseRequest: Rendering order section with order numbers:', response.orderNumbers);
                    renderOrderSection(response.orderNumbers, memberNum);
                    $('#orderSection').css("margin-top", "40px").slideDown();
                    $('#completeOrderButton').show();
                    $('html, body').animate({
                        scrollTop: $('#orderSection').offset().top
                    }, 500);
                    
                    // 체크박스와 삭제 버튼을 비활성화
                    $('#selectAll').prop('disabled', true);  // 전체 선택 체크박스 비활성화
                    $('#deleteAllBtn').prop('disabled', true); // 선택된 상품 삭제 버튼 비활성화
                    $('#purchaseButton').hide(); // 구매하기 버튼 숨기기
                    $('.remove-btn').each(function() {
                        $(this).prop('disabled', true);
                    });
                    renderCart(true);
                });
            }
        },
        error: function(xhr, status, error) {
            console.error('sendPurchaseRequest: Error occurred:', error);
            showAlert('error', '구매 요청 실패', '구매 요청 중 오류가 발생했습니다.');
        }
    });
}
*/
/*
function renderOrderSection(orderNumbers, memberNum) {
    // orderNumbers 배열을 문자열 배열로 변환하여 서버로 전송
    const orderNumbersStr = orderNumbers.map(String);
    console.log('renderOrderSection: Sending request to fetch purchased products with order numbers:', orderNumbersStr, 'and memberNum:', memberNum);
    
    $.ajax({
        url: 'getPurchasedProducts.do',
        type: 'POST',
        contentType: 'application/json',
        data: JSON.stringify({ orderNumbers: orderNumbersStr, memberNum: memberNum }),
        dataType: 'json',
        success: function(purchasedProductNums) {
            console.log('renderOrderSection: Purchased product numbers received:', purchasedProductNums);
            
            const productListContainer = $('#orderSection .order-products');
            productListContainer.empty();
            productListContainer.append('<h5>구매한 상품 목록</h5>');

            // 장바구니 세션 데이터(productList)에서 유효한 productNum과 일치하는 상품만 화면에 표시
            productList.forEach(product => {
                if (purchasedProductNums.includes(product.productNum)) {
                    console.log('renderOrderSection: Displaying product:', product);
                    
                    const productItem = `
                        <div class="order-product-item mb-3">
                            <strong>${product.productName}</strong><br>
                            ${product.productPrice}P x ${product.quantity}개 = ${product.productPrice * product.quantity}P
                        </div>
                    `;
                    productListContainer.append(productItem);
                }
            });
        },
        error: function(xhr, status, error) {
            console.error('renderOrderSection: Error fetching purchased products:', error);
            showAlert('error', '구매 목록 불러오기 실패', '구매 목록을 가져오는 중 오류가 발생했습니다.');
        }
    });
}
*/



