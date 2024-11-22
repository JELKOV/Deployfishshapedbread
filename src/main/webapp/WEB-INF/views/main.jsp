<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<%@ taglib prefix="custom" tagdir="/WEB-INF/tags"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"%>
<c:set var="path" value="${pageContext.request.contextPath}" />
<!DOCTYPE HTML>

<html>
<head>
<title>Main</title>
<meta charset="utf-8" />
<meta name="viewport"
	content="width=device-width, initial-scale=1, user-scalable=no" />
<!-- 파비콘 -->
<link rel="icon" href="${path}/resources/assets/images/logo.png"
	type="image/x-icon" />

<!-- css -->
<link rel="stylesheet" href="${path}/resources/assets/css/mainPage.css">
<link rel="stylesheet"
	href="${path}/resources/assets/css/mainPageImgSlide.css">
<link rel="stylesheet"
	href="${path}/resources/assets/css/mainPageSection2.css">
<link rel="stylesheet"
	href="${path}/resources/assets/css/mainPageSection3.css">
<link rel="stylesheet"
	href="${path}/resources/assets/css/mainPageSection4.css">
<link rel="stylesheet"
	href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.0.0-beta3/css/all.min.css">
<link
	href="https://fonts.googleapis.com/css2?family=Gamja+Flower&display=swap"
	rel="stylesheet">
<link rel="stylesheet"
	href="${path}/resources/assets/css/footer.css">

<!-- 맵을 그리기 위한 script -->
<!-- appkey에 발급받은 APP KEY를 넣음 -->
<!-- 추가 기능 사용 시 &libraries=services 코드 추가(주소를 좌표로) -->
<script type="text/javascript"
    src="//dapi.kakao.com/v2/maps/sdk.js?appkey=${mapApiKey}&libraries=services"></script>


<style>
/* 각 섹션별 배경 이미지 */
#section1 {
	background: url('${path}/resources/assets/images/main1.jpeg') center
		center/cover no-repeat;
	background-size: cover;
	position: relative;
}
#section1::before {
	content: "";
	position: absolute;
	background-color: rgba(249, 224, 154, 0.4);
	top: 0;
	left: 0;
	right: 0;
	bottom: 0;
	pointer-events: none;
}

#section2 {
	background-color: #f9e09a;
	background-size: cover;
}

#section3 {
	background-color: #fcf4d9;
	background-size: cover;
}

#section4 {
	background: url('${path}/resources/assets/images/main4.jpg') center
		center/cover no-repeat;
	background-size: cover;
}
</style>
</head>
<body>
	<!-- 헤더 -->
	<!-- Header (고정) -->
	<div id="header-wrapper">
		<custom:header />
	</div>

	<div id="page-wrapper">
		<!-- 섹션 1: 로고, 이미지, 슬로건 -->
		<section id="section1" class="full-page">
			<div class="content text-center">
				<div class="text-box fade-in-right">
					<!-- 초기 로드 시 fade-in-right 적용 -->
					<img src="${path}/resources/assets/images/logo.png" alt="logoImage"
						class="logo-image" />
					<h1>붕어빵 원정대</h1>
					<h2>따뜻한 붕어빵을 찾아 떠나는 여정에 오신 것을 환영합니다!</h2>
					<h3>붕어빵을 찾아서 여행을 떠나보세요.</h3>
				</div>
			</div>
			<!-- 오른쪽에서 로딩되어 차례로 나타나는 이미지들 -->
			<div class="image-sequence">
				<div class="image-text-slide image-1">
					<a href="#section2" class="image-link"> <img
						src="${path}/resources/assets/images/main1-1.jpg" alt="image1"
						class="image-slide" />
						<div class="image-text">주변 가게 검색</div>
					</a>
				</div>
				<div class="image-text-slide image-2">
					<a href="#section3" class="image-link"> <img
						src="${path}/resources/assets/images/main1-2.jpg" alt="image2"
						class="image-slide" />
						<div class="image-text">상품 및 게시글</div>
					</a>
				</div>
				<div class="image-text-slide image-3">
					<a href="#section4" class="image-link"> <img
						src="${path}/resources/assets/images/main1-3.jpg" alt="image3"
						class="image-slide" />
						<div class="image-text">프로젝트 소개</div>
					</a>
				</div>
			</div>
			<div class="content text-center">
				<a href="loadListStore.do" class="second-text-box fade-in-left"
					style="text-decoration: none; color: inherit;"> <img
					src="${path}/resources/assets/images/main1Map.jpg" alt="mainMap"
					class="main-image" />
					<h2>
						<i class="fas fa-map-marker-alt"></i> 지도를 통해 붕어빵을 찾아 보세요
					</h2>
				</a>
			</div>
		</section>


		<!-- 섹션 2: 지도와 가게 검색 -->
		<section id="section2" class="full-page">
			<div id="addressSearchMap">
				<div class="content text-center">
					<h1>가게 위치와 검색</h1>
					<p>가까운 붕어빵 트럭을 검색하고 위치를 확인하세요.</p>
				</div>
			</div>
			<img src="${path}/resources/assets/images/map_cat.png" alt="map_cat Image" class="mapSideImage" />
		</section>

		<!-- 섹션 3: 상품 및 게시물 -->
		<section id="section3" class="full-page">
			<div class="content">
				<h1 class="section-title">
					<i class="fas fa-shopping-bag"></i> 상품 및 게시물
				</h1>
				<div class="section3-grid">
					<!-- 1행: 배너 -->
					<div class="banner-row" colspan="3">
						<div class="banner-container">
							<div class="banner-slide">
								<img src="${path}/resources/assets/images/banner/banner1.png"
									alt="Banner Image" class="banner-image" /> <img
									src="${path}/resources/assets/images/banner/banner2.png"
									alt="Banner Image" class="banner-image" /> <img
									src="${path}/resources/assets/images/banner/banner3.png"
									alt="Banner Image" class="banner-image" /> <img
									src="${path}/resources/assets/images/banner/banner4.png"
									alt="Banner Image" class="banner-image" />
							</div>
						</div>
					</div>

					<!-- 2행 1열: 상품 페이지 링크 -->
					<div class="link-box">
						<a href="loadListProduct.do"> <img
							src="${path}/resources/assets/images/main3Product.jpg"
							alt="상품 페이지로 이동" class="link-image" />
							<p class="link-text">상품 페이지 이동</p>
						</a>
					</div>

					<!-- 2행 2열: 커뮤니티 게시판 링크 -->
					<div class="link-box">
						<a href="loadListBoards.do?boardCategoryName=boardList"> <img
							src="${path}/resources/assets/images/main3Community.jpg"
							alt="커뮤니티 게시판으로 이동" class="link-image" />
							<p class="link-text">게시판 이동</p>
						</a>
					</div>

					<!-- 2행 3열: 인기 게시물 TOP 3 -->
					<div class="posts-column">
						<h3>인기 게시물 TOP 3</h3>
						<ul class="popular-posts">
							<c:forEach var="post" items="${hotBoardList}" varStatus="status">
								<li>
									<h4>Top ${status.index + 1}</h4>
									<p>
										<strong>${post.boardTitle}</strong> - ${post.memberNickname}
									</p>
									<p>${fn:length(post.boardContent) > 30 ? fn:substring(post.boardContent, 0, 30) + '...' : post.boardContent}</p>
									<a href="infoBoard.do?boardNum=${post.boardNum}"
									class="btn btn-secondary">게시글 보러 가기</a>
								</li>
							</c:forEach>
						</ul>
					</div>
				</div>
			</div>
		</section>

		<!-- 섹션 4: 팀과 프로젝트 소개 -->
		<section id="section4" class="full-page">
			<div class="content text-center">
				<h2>우리 팀 소개</h2>
				<p>붕어빵 원정대를 만들어가는 팀원들과 개발 내역을 소개합니다.</p>

				<!-- 1행: 프로젝트 소개, 팀 소개, 개발 툴 -->
				<div class="row">
					<div class="col">
						<h3>프로젝트 소개</h3>
						<p>붕어빵 원정대 프로젝트는 붕어빵 가게를 찾고, 추천하는 서비스입니다.</p>
					</div>
					<div class="col">
						<h3>팀 소개</h3>
						<ul class="team-list">
							<li>[팀장] 정재준</li>
							<li>[M 파트장] 조충현</li>
							<li>[C 파트장] 한지윤</li>
							<li>[V 파트장] 고미진</li>
							<li>[V 팀원] 안재호</li>
							<li>[V 팀원] 정재희</li>
						</ul>
					</div>
					<div class="col">
						<h3>개발 툴</h3>
						<ul class="tools-list">
							<li><i class="fab fa-java"></i> Java</li>
							<li><i class="fas fa-leaf"></i> Spring Framework</li>
							<li><i class="fas fa-database"></i> MySQL</li>
							<li><i class="fab fa-js-square"></i> JavaScript</li>
							<li><i class="fab fa-html5"></i> HTML</li>
							<li><i class="fab fa-css3-alt"></i> CSS</li>
						</ul>
					</div>
				</div>

				<!-- 2행: 프로젝트 일정 타임라인 -->
				<h2>프로젝트 일정 타임라인</h2>
				<div class="timeline">
					<div class="timeline-item left">
						<div class="timeline-content">
							<span class="timeline-date">2024-07</span>
							<div class="icon">&#128187;</div>
							<p>프로젝트 아이디어 구상 및 기획</p>
						</div>
					</div>
					<div class="timeline-item right">
						<div class="timeline-content">
							<span class="timeline-date">2024-08</span>
							<div class="icon">&#128736;</div>
							<p>프로젝트 초기 설계 및 파트별 개발 시작</p>
						</div>
					</div>
					<div class="timeline-item left">
						<div class="timeline-content">
							<span class="timeline-date">2024-09</span>
							<div class="icon">&#127942;</div>
							<p>프로젝트 중간 발표</p>
						</div>
					</div>
					<div class="timeline-item right">
						<div class="timeline-content">
							<span class="timeline-date">2024-10</span>
							<div class="icon">&#128295;</div>
							<p>프로젝트 테스트 및 버그 수정</p>
						</div>
					</div>
					<div class="timeline-item left">
						<div class="timeline-content">
							<span class="timeline-date">2024-11</span>
							<div class="icon">&#128640;</div>
							<p>최종 검토 및 발표 프로젝트 완료</p>
						</div>
					</div>
				</div>
			</div>
		</section>

	</div>

	<!-- 푸터 -->
	<custom:footer />

	<!-- 스크립트 -->
	<script src="${path}/resources/assets/js/jquery.min.js"></script>
	<script src="${path}/resources/assets/js/browser.min.js"></script>
	<script src="${path}/resources/assets/js/util.js"></script>
	<script src="${path}/resources/assets/js/mainScroll.js"></script>
	<script src="${path}/resources/assets/js/mainBanner.js"></script>
	<script src="${path}/resources/assets/js/map/mainMap.js"></script>

</body>
</html>