<%@ page language="java" contentType="text/html; charset=utf-8" pageEncoding="utf-8"%>
<html lang="en">

    <head>
    
 	   <!-- 제이쿼리 -->
    	<script src="https://code.jquery.com/jquery-3.6.0.min.js"></script>
    
        <!-- scripts -->
        <script src="/resources/assets/js/theme.bundle.min.js"></script>
        <!-- Required meta tags -->
        <meta charset="utf-8">
        <meta name="viewport" content="width=device-width, initial-scale=1">
        <link rel="icon" href="/resources/assets/img/favicon.ico" type="image/ico">
        <!--Box Icons-->
        <link rel="stylesheet" href="/resources/assets/fonts/boxicons/css/boxicons.min.css">

        <!--AOS Animations-->
        <link rel="stylesheet" href="/resources/assets/vendor/node_modules/css/aos.css">

        <!--Iconsmind Icons-->
        <link rel="stylesheet" href="/resources/assets/fonts/iconsmind/iconsmind.css">

        <!--Google fonts-->
        <link rel="preconnect" href="https://fonts.googleapis.com">
        <link rel="preconnect" href="https://fonts.gstatic.com" crossorigin>
        <link href="https://fonts.googleapis.com/css2?family=Poppins:ital,wght@0,200;0,300;0,400;0,500;0,600;0,700;1,400&family=Source+Serif+Pro:ital@0;1&display=swap" rel="stylesheet">
        <!-- Main CSS -->
        <link href="/resources/assets/css/theme.min.css" rel="stylesheet">

        <title>멘토링 인서트폼</title>
    </head>

    <body data-bs-spy="scroll" data-bs-target="#navbar-elements">

        <!--Header Start-->
        
        <!--Header End-->
       
        <!--Main content-->	
        <main>
            <section class="position-relative">
                <div class="container py-9 py-lg-11">
                    <div class="row justify-content-between" style="margin-top: 30px; margin-left: 300px;">
                        <div class="col-md-9 col-xl-8 order-md-1">
                            <!--Input-->
                            
                          <form method="post" action="/coco/mentoring/mentoringInsert" enctype="multipart/form-data">
                           	<input type="hidden" name="expertId" value="${member.memId }">
                           	<input type="hidden" name="memId" value="${member.memId }">
                           	<input type="hidden" name="memNick" value="${member.memNick }">
                            <h6 class="mb-3">솔루션 카테고리</h6>
                            <input type="radio" class="btn-check" id="plan-monthly" name="eprodCateNum" disabled="disabled" value="템플릿">
                            <label class="btn btn-outline-primary" for="plan-monthly" >템플릿</label>
                            <input type="radio" class="btn-check" id="plan-yearly" name="eprodCateNum" checked="checked" readonly="readonly" value="멘토링">
                            <label class="btn btn-outline-primary" for="plan-yearly">멘토링</label>
                            <hr class="mt-5 mb-3">                               
                            

            
                            
                            <!-- 이 부분은 session에서 멘토 닉네임을 가져와서 disable설정 -->
							<h6 class="mb-3">멘토 닉네임</h6>
                            <input type="text" class="form-control" disabled="disabled" value="${member.memNick }">							
                            <hr class="mt-5 mb-3">   

                            
							<h6 class="mb-3">상품명</h6>
                            <input type="text" class="form-control" placeholder="Default Input" name="eprodName">							
                            <hr class="mt-5 mb-3">                                                      
                            
                            
                            
							<h6 class="mb-3">상품 가격</h6>
                            <div class="input-group mb-3">
                                <span class="input-group-text">￦</span>
                                <input type="text" class="form-control" aria-label="Amount (to the nearest dollar)" name="eprodPrice">
                            </div>                            
                            <hr class="mt-5 mb-3">     


							<h6 class="mb-3">멘토링 카테고리 선택</h6>                            
                            <div class="form-check mb-1">
                                <input class="form-check-input" type="radio" checked="checked" name="eprodTag" id="flexRadioDefault1" value="programming" >
                                <label class="form-check-label" for="flexRadioDefault1"> 개발·프로그래밍 </label>
                            </div>
                            <div class="form-check mb-1">
                                <input class="form-check-input" type="radio" name="eprodTag" id="flexRadioDefault1" value="security">
                                <label class="form-check-label" for="flexRadioDefault1"> 보안·네트워크 </label>
                            </div>
                            <div class="form-check mb-1">
                                <input class="form-check-input" type="radio" name="eprodTag" id="flexRadioDefault1" value="datascience">
                                <label class="form-check-label" for="flexRadioDefault1"> 데이터사이언스 </label>
                            </div>
                            <div class="form-check mb-1">
                                <input class="form-check-input" type="radio" name="eprodTag" id="flexRadioDefault1" value="game">
                                <label class="form-check-label" for="flexRadioDefault1"> 게임개발 </label>
                            </div>
                            <div class="form-check mb-1">
                                <input class="form-check-input" type="radio" name="eprodTag" id="flexRadioDefault1" value="hardware">
                                <label class="form-check-label" for="flexRadioDefault1"> 하드웨어 </label>
                            </div>
                            <hr class="mt-5 mb-3">     
                                							
                                							
							<input type="hidden" name="eprodLangCode" value="null"> 
							
                                							
                            <h6>멘토링 상세 설명</h6>
                            <div class="form-floating">
                                <textarea class="form-control"  placeholder="Leave a comment here" 
                                		  style="height: 100px" id="floatingTextarea2" name="eprodSummary"></textarea>
                                <label for="floatingTextarea5">멘토링 상세과정을 작성해주세요(시간, 내용 등을 상세히 적을 수록 판매에 도움이 됩니다)</label>
                            </div>

                            <hr class="mt-5 mb-3">                                							
                            <div class="mb-3">
                            <h6>상품 파일<span style="font-size: 80%;">　(첫번째 사진은 대표 썸네일로 사용됩니다.)</span></h6>
                                <input class="form-control" type="file" name="postFiles" id="formFileMultiple" multiple="">
                            </div>                            
														<div style="position: absolute; left: 56.5%">
	                            <button type="button" id="cancel" class="btn btn-outline-primary">취소하기</button>
	                            <button type="submit" class="btn btn-outline-primary">등록하기</button>
	                        </div>
                       </form>
                     </div>
                   </div>
                </div>
            </section>
        </main>

 

        <!-- begin Back to Top button -->
        <a href="#" class="toTop">
            <i class="bx bxs-up-arrow"></i>
        </a>

<script type="text/javascript">
$("#cancel").on("click", function() {
	  window.location.href = "/coco/mentoring/category";
	  console.log("작동되고있어요");
	});
</script>

    </body>

</html>
