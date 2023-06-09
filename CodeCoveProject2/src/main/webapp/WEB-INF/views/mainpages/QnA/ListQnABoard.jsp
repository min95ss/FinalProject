<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn" %>

<div class="container-fluid pt-9">
	<h4 class="h4 text-center pb-3">문의사항 게시판</h4>
	<div class="row mb-3 mb-lg-5">
		<div class="card">
			<form method="POST" action="/coco/QnABoard" id="searchForm" name="searchForm">
				<div class="row d-flex justify-content-end ">
					<div class="col-sm-3 mx-2 my-2">
						<input type="hidden" id="page" name="page">
						<input type="text" id="searchWord" name="searchWord" class="form-control text-center" placeholder="검색어를 입력해주세요" value="${searchWord }">
					</div>
				</div>
			</form>
			<div class="table-responsive">
				<table id="datatable" class="table mt-0 table-striped table-card table-nowrap px-2 py-2">
					<thead class="text-uppercase small text-body-secondary">
						<tr>
							<th class="text-center">순번</th>
							<th class="text-center">제목</th>
							<th class="text-center">작성자</th>
							<th class="text-center">작성일</th>
							<th class="text-center">조회수</th>
						</tr>
					</thead>
					<tbody id="tbody">
						<c:set value="${pagingVO.dataList }" var="qnaList"/>
						<c:set value="${start }" var="num"/>
						<c:choose>
							<c:when test="${empty qnaList }">
								<tr>
									<td>조회할 게시글이 없습니다.</td>
								</tr>
							</c:when>
							<c:otherwise>
								<c:forEach items="${qnaList }" var="qna" varStatus="stat">
									<tr id="trs">
										<td style="display: none;">
											<span id="qnaValue">${qna.qnaNum }</span>
										</td>
										<td style="display: none;">
											<span id="qnaPass">${qna.qnaPass }</span>
										</td>
										<td class="text-center">${num }</td>
										<td>${qna.qnaTitle }<span id="ynValue">${qna.qnaPublicYn }</span></td>
										<td class="text-center">${qna.writerId }</td>
										<td class="text-center">
											<fmt:formatDate value="${qna.qnaDate }" pattern="yyyy-MM-dd"/>
										</td>
										<td class="text-center">${qna.viewNum }</td>
									</tr>
									<c:set value="${num - 1 }" var="num"/>
								</c:forEach>
							</c:otherwise>
						</c:choose>
					</tbody>
				</table>
				<div class="row mx-0">
					<div class="d-flex justify-content-end">
						<button type="button" id="registerQnA" name="registerQnA" class="btn btn-primary mx-0">등록</button>
					</div>
				</div>
			</div>
			<div class="card-footer clearfix mt-2" id="pagingArea">
				${pagingVO.pagingHTML }
			</div>
		</div>
	</div>
</div>


<div class="modal fade" id="modalForm" tabindex="-1"
	aria-labelledby="modalFormLabel" aria-hidden="true">
	<div class="modal-dialog">
		<div class="modal-content border-0">
			<div class="position-relative border-0 pe-4">
				<button type="button"
					class="btn btn-gray-200 p-0 border-2 width-3x height-3x rounded-circle flex-center position-absolute end-0 top-0 mt-3 me-3 z-1"
					data-bs-dismiss="modal" aria-label="Close">
					<i class="bx bx-x fs-5"></i>
				</button>
			</div>
			<div class="modal-body py-5 border-0">
				<div class="px-3">
					<h3>비밀번호 입력</h3>
					<div>
						<input type="text" id="inputPass" class="form-control" value=""
							placeholder="게시글 작성 시 입력한 비밀번호를 입력해주세요.">
					</div>
					<div class="d-grid">
						<button class="btn" id="subBtn" type="button">제출</button>
						<button class="btn" type="button" data-bs-dismiss="modal">취소</button>
					</div>
				</div>
			</div>
		</div>
	</div>
</div>

<input type="hidden" id="hPass" value="">
<input type="hidden" id="hVal" value="">

<script src="https://code.jquery.com/jquery-3.6.0.min.js"></script>
<script type="text/javascript">
const modalForm = $("#modalForm");
function f_openModal() {
	trs.attr('data-bs-toggle', 'modal');
	trs.attr('data-bs-target', '#modalForm');
	modalForm.modal('show');
};

	$(function() {
		const pagingArea = $("#pagingArea");
		
		const searchForm = $("#searchForm");
		
		const tbody = $("#tbody");
		const trs = $("#trs");
		const hPass = $("#hPass");
		const hVal = $("#hVal");
		const registerQnA = $("#registerQnA");
		const subBtn = $("#subBtn");

		tbody.on("click", "#trs", function() {
			// YN체크
			let ynVal = $(this).find("#ynValue").html();
			// DB -> VO -> TD(hidden) 게시물식별CODE 가져옴
			let qnaValue = $(this).find("#qnaValue").text();
			// DB -> VO -> TD(hidden) PASS 가져옴
			let qnaPass = $(this).find("#qnaPass").html();
			hPass.val(qnaPass);
			hVal.val(qnaValue);

			if (ynVal == "Y") {
				location.href = "/coco/QnABoardRead?qnaNum=" + qnaValue;
			} else {
				f_openModal();
			}
		});

		// 비밀번호 체크 모달(비밀번호 제출)
		subBtn.on("click", function() {
			let orginPass = $("#hPass").val();
			let qnaValue = $("#hVal").val();
			let inputPass = $("#inputPass").val();

			if (orginPass == inputPass) {
				location.href = "/coco/QnABoardRead?qnaNum=" + qnaValue;
			} else {
				alert("비밀번호가 일치하지 않습니다.")
			}

		});

		registerQnA.on("click", function() {
			location.href = "/coco/QnARegisterForm";
		});
		
		// 페이지네이션, 단어검색
		pagingArea.on("click", "a", function(event){
			event.preventDefault();
			var pageNo = $(this).data("page");
			searchForm.find("#page").val(pageNo);
			searchForm.submit();
		});

	});
</script>