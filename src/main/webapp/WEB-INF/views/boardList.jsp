<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"
	import="java.util.ArrayList, java.util.HashMap, java.util.Map, java.util.List"%>
<%@ taglib prefix="custom" tagdir="/WEB-INF/tags"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"%>
<c:set var="path" value="${pageContext.request.contextPath}" />
<!DOCTYPE html>
<html>
<head>
<!-- css 변경을 위한 캐시 방지 -->
<meta http-equiv="Cache-Control"
	content="no-cache, no-store, must-revalidate">
<meta http-equiv="Pragma" content="no-cache">
<meta http-equiv="Expires" content="0">

<title>게시글</title>
<meta charset="utf-8" />
<!-- 파비콘 -->
<link rel="icon" href="${path}/resources/assets/images/logo.png"
	type="image/x-icon" />

<link rel="stylesheet" href="${path}/resources/assets/css/main.css">
<link rel="stylesheet" href="${path}/resources/assets/css/board/boardlist.css">
<link rel="stylesheet" href="${path}/resources/assets/css/searchbar.css">
<link rel="stylesheet"
	href="${path}/resources/assets/css/pagination.css">

<!-- bootstrap -->
<link rel="stylesheet"
	href="https://stackpath.bootstrapcdn.com/bootstrap/4.5.2/css/bootstrap.min.css">
<link rel="stylesheet"
	href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/css/bootstrap.min.css"
	integrity="sha384-QWTKZyjpPEjISv5WaRU9OFeRpok6YctnYmDr5pNlyT2bRjXh0JMhjY6hW+ALEwIH"
	crossorigin="anonymous">
</head>
<body>
	<div id="page-wrapper">
		<script src="https://cdn.jsdelivr.net/npm/sweetalert2@11"></script>
		<script src="https://code.jquery.com/jquery-latest.min.js"></script>
		<script src="${path}/resources/assets/js/board/board.js"></script>


		<custom:header />

		<!--  -->
		<div class="container">
			<form action="loadListBoards.do" method="GET">
				<!-- 게시글카테고리(문의/일반) -->
				<input type="hidden" name="boardCategoryName"
					value="${boardCategoryName}">
				<!-- 첫 번째 행 -->
				<br>
				<br>
				<div class="row">
					<!-- C에서 온 카테고리가 일반이라면 -->
					<c:if test="${boardCategoryName eq 'boardList'}">
						<custom:pageTitle>일반 게시판</custom:pageTitle>
					</c:if>
					<!-- 에서 온 카테고리가 문의이라면 -->
					<c:if test="${boardCategoryName eq 'noticeBoard'}">
						<custom:pageTitle>문의 게시판</custom:pageTitle>
					</c:if>
				</div>

				<!-- 두 번째 행 -->
				<div class="row align-items-center">
					<!-- search line -->
					<div class="col-3 justify-content-center">
						<div class="categoryBox">
							<!-- 카테고리 -->
							<select id="contentFilter" name="contentFilter"
								class="category half" required>
								<%--<option value="category" disabled selected>카테고리</option>--%>
								<option value="SELECT_PART_TITLE"
									${contentFilter eq 'SELECT_PART_TITLE'? 'selected' : ''}>제목</option>
								<option value="SELECT_PART_CONTENT"
									${contentFilter eq 'SELECT_PART_CONTENT'? 'selected' : ''}>내용</option>
								<option value="SELECT_PART_NICKNAME"
									${contentFilter eq 'SELECT_PART_NICKNAME'? 'selected' : ''}>닉네임</option>
							</select>
							<!-- 날짜 -->
							<select id="condition" name="writeDayFilter"
								class="category half" required>
								<option value="ALL"
									${writeDayFilter eq 'ALL' || empty writeDayFilter ? 'selected' : ''}>기간</option>
								<option value="7" ${writeDayFilter eq '7'? 'selected' : ''}>7일</option>
								<option value="15" ${writeDayFilter eq '15'? 'selected' : ''}>15일</option>
								<option value="30" ${writeDayFilter eq '30'? 'selected' : ''}>30일</option>
							</select>
						</div>
					</div>
					<div class="col-8">
					<custom:searchbar placeholder="검색어를 입력해주세요."/>
						
					</div>

					<div class="col-1 text-end justify-content-center">
						<c:choose>
							<c:when test="${empty userPK}">
								<a href="login.do" class="btn button-orange nowrap" role="button"
									id="addBoard" name="addBoard">글쓰기</a>
							</c:when>
							<c:otherwise>
								<a href="addBoard.do?boardCategoryName=${boardCategoryName}"
									class="btn button-orange nowrap" role="button" id="addBoard"
									name="addBoard">글쓰기</a>
							</c:otherwise>
						</c:choose>
					</div>
				</div>
			</form>
			<!-- 세 번째 행 -->
			<div class="row">
				<div class="col-12">
					<table class="customTable">
						<thead>
							<tr>
								<th class="tableDate">작성일자</th>
								<th class="tableNum">번호</th>
								<th class="tableTitle">제목</th>
								<th class="tableWriter">작성자</th>
								<th class="tableReplyNum">댓글 수</th>
								<th class="tableLike">좋아요 수</th>
							</tr>
						</thead>
						<tbody>
							<%--인기글--%>
							<c:forEach var="board" items="${hotBoardList}">
								<tr
									onclick="location.href='infoBoard.do?boardNum=${board.boardNum}'">
									<!-- 작성일자 -->
									<td align="center">${board.boardWriteDay}</td>
									<!-- 고유번호  -->
									<td align="center">${board.boardNum}</td>
									<!-- 제목 -->
									<td align="center">${board.boardTitle}</td>
									<!-- 작성자 -->
									<td align="left">${board.memberNickname}</td>
									<!-- 댓글수 -->
									<td align="left">${board.replyCnt}</td>
									<!-- 좋아요수 -->
									<td align="left"><svg xmlns="http://www.w3.org/2000/svg"
											width="16" height="16" fill="currentColor"
											class="bi bi-hand-thumbs-up-fill" viewBox="0 0 16 16">
										<path
												d="M6.956 1.745C7.021.81 7.908.087 8.864.325l.261.066c.463.116.874.456 1.012.965.22.816.533 2.511.062 4.51a10 10 0 0 1 .443-.051c.713-.065 1.669-.072 2.516.21.518.173.994.681 1.2 1.273.184.532.16 1.162-.234 1.733q.086.18.138.363c.077.27.113.567.113.856s-.036.586-.113.856c-.039.135-.09.273-.16.404.169.387.107.819-.003 1.148a3.2 3.2 0 0 1-.488.901c.054.152.076.312.076.465 0 .305-.089.625-.253.912C13.1 15.522 12.437 16 11.5 16H8c-.605 0-1.07-.081-1.466-.218a4.8 4.8 0 0 1-.97-.484l-.048-.03c-.504-.307-.999-.609-2.068-.722C2.682 14.464 2 13.846 2 13V9c0-.85.685-1.432 1.357-1.615.849-.232 1.574-.787 2.132-1.41.56-.627.914-1.28 1.039-1.639.199-.575.356-1.539.428-2.59z" />
									</svg> &nbsp ${board.likeCnt}</td>
								</tr>
							</c:forEach>
							<%--일반 게시글--%>
							<c:if test="${empty boardList}">
								<tr>
									<td colspan="6">검색 결과가 없습니다.</td>
								</tr>
							</c:if>
							<c:if test="${not empty boardList}">
								<c:forEach var="board" items="${boardList}">
									<tr
										onclick="location.href='infoBoard.do?boardNum=${board.boardNum}'">
										<!-- 작성일자 -->
										<td align="center">${board.boardWriteDay}</td>
										<!-- 고유번호  -->
										<td align="center">${board.boardNum}</td>
										<!-- 제목 -->
										<td align="center">${board.boardTitle}</td>
										<!-- 작성자 -->
										<td align="left">${board.memberNickname}</td>
										<!-- 댓글수 -->
										<td align="left">${board.replyCnt}</td>
										<!-- 좋아요수 -->
										<td align="left"><svg xmlns="http://www.w3.org/2000/svg"
												width="16" height="16" fill="currentColor"
												class="bi bi-hand-thumbs-up-fill" viewBox="0 0 16 16">
											<path
													d="M6.956 1.745C7.021.81 7.908.087 8.864.325l.261.066c.463.116.874.456 1.012.965.22.816.533 2.511.062 4.51a10 10 0 0 1 .443-.051c.713-.065 1.669-.072 2.516.21.518.173.994.681 1.2 1.273.184.532.16 1.162-.234 1.733q.086.18.138.363c.077.27.113.567.113.856s-.036.586-.113.856c-.039.135-.09.273-.16.404.169.387.107.819-.003 1.148a3.2 3.2 0 0 1-.488.901c.054.152.076.312.076.465 0 .305-.089.625-.253.912C13.1 15.522 12.437 16 11.5 16H8c-.605 0-1.07-.081-1.466-.218a4.8 4.8 0 0 1-.97-.484l-.048-.03c-.504-.307-.999-.609-2.068-.722C2.682 14.464 2 13.846 2 13V9c0-.85.685-1.432 1.357-1.615.849-.232 1.574-.787 2.132-1.41.56-.627.914-1.28 1.039-1.639.199-.575.356-1.539.428-2.59z" />
										</svg> &nbsp ${board.likeCnt}</td>
									</tr>
								</c:forEach>
							</c:if>
						</tbody>
					</table>
				</div>
			</div>

			<!-- 네 번째 행 -->
			<div class="row">
				<div class="col-12">
					<!-- 페이지네이션 -->
					<section id="pagination">
						<div class="pagination">
							<!-- 이전 페이지 버튼 -->
							<c:if test="${page > 1}">
								<%--url을 받아와 page가 있다면 변경하고 없다면 추가, 받아올 url이 없는 경우에도 추가--%>
								<!-- 이전 페이지 -->
								<c:set var="queryWithNewPage"
									value="${empty queryString ? 'page='.concat(page - 1) :
									  (fn:contains(queryString, 'page=') ?
										 fn:replace(queryString, 'page='.concat(page), 'page='.concat(page - 1)) :
										 queryString.concat('&page=').concat(page - 1))}" />
								<a href="?${queryWithNewPage}" id="pagenationPreValue">&laquo;
									이전</a>
							</c:if>

							<c:set var="startPage" value="${page - 5}" />
							<c:set var="endPage" value="${page + 4}" />

							<c:if test="${startPage < 1}">
								<c:set var="startPage" value="1" />
							</c:if>
							<c:if test="${endPage > totalPage}">
								<c:set var="endPage" value="${totalPage}" />
							</c:if>

							<c:forEach var="i" begin="${startPage}" end="${endPage}">
								<c:choose>
									<c:when test="${i == page}">
										<strong>${i}</strong>
									</c:when>
									<c:otherwise>
										<%--url을 받아와 page가 있다면 변경하고 없다면 추가, 받아올 url이 없는 경우에도 추가--%>
										<c:set var="queryWithNewPage"
											value="${empty queryString ? 'page='.concat(i) :
											  (fn:contains(queryString, 'page=') ?
												 fn:replace(queryString, 'page='.concat(page), 'page='.concat(i)) :
												 queryString.concat('&page=').concat(i))}" />
										<a href="?${queryWithNewPage}" class="pagenationValue">${i}</a>
									</c:otherwise>
								</c:choose>
							</c:forEach>

							<c:if test="${page < totalPage}">
								<%--url을 받아와 page가 있다면 변경하고 없다면 추가, 받아올 url이 없는 경우에도 추가--%>
								<c:set var="queryWithNewPage"
									value="${empty queryString ? 'page='.concat(page + 1) :
									  (fn:contains(queryString, 'page=') ?
										 fn:replace(queryString, 'page='.concat(page), 'page='.concat(page + 1)) :
										 queryString.concat('&page=').concat(page + 1))}" />
								<a href="?${queryWithNewPage}" id="pagenationNextValue">다음
									&raquo;</a>
							</c:if>
						</div>
					</section>
					<!-- 페이지네이션 종료 -->
				</div>
			</div>
		</div>
		<custom:footer />
	</div>
</body>
</html>