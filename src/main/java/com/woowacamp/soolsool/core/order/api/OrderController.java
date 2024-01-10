package com.woowacamp.soolsool.core.order.api;

import com.woowacamp.soolsool.core.member.dto.LoginUser;
import com.woowacamp.soolsool.core.member.dto.NoAuth;
import com.woowacamp.soolsool.core.order.application.OrderCommandService;
import com.woowacamp.soolsool.core.order.application.OrderQueryService;
import com.woowacamp.soolsool.core.order.dto.response.OrderDetailResponse;
import com.woowacamp.soolsool.core.order.dto.response.OrderRatioResponse;
import com.woowacamp.soolsool.core.order.dto.response.PageOrderListResponse;
import com.woowacamp.soolsool.core.order.exception.OrderResultCode;
import com.woowacamp.soolsool.global.aop.RequestLogging;
import com.woowacamp.soolsool.global.common.ApiResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import static com.woowacamp.soolsool.core.order.exception.OrderResultCode.ORDER_DETAIL_SUCCESS;

@RestController
@Slf4j
@RequestMapping("/orders")
@RequiredArgsConstructor
public class OrderController {

    private final OrderCommandService orderCommandService;
    private final OrderQueryService orderQueryService;

    @RequestLogging
    @GetMapping("/{orderId}")
    public ResponseEntity<ApiResponse<OrderDetailResponse>> orderDetail(
            @LoginUser final Long memberId,
            @PathVariable final Long orderId
    ) {
        final OrderDetailResponse response = orderQueryService.orderDetail(memberId, orderId);

        return ResponseEntity.ok(ApiResponse.of(ORDER_DETAIL_SUCCESS, response));
    }

    @RequestLogging
    @GetMapping
    public ResponseEntity<ApiResponse<PageOrderListResponse>> orderList(
            @LoginUser final Long memberId,
            @PageableDefault final Pageable pageable,
            @RequestParam(required = false) final Long cursorId
    ) {
        final PageOrderListResponse response = orderQueryService.orderList(memberId, pageable, cursorId);

        return ResponseEntity.ok(ApiResponse.of(ORDER_DETAIL_SUCCESS, response));
    }

    @NoAuth
    @RequestLogging
    @GetMapping("/ratio")
    public ResponseEntity<ApiResponse<OrderRatioResponse>> getOrderRatioByLiquorId(
            @RequestParam final Long liquorId
    ) {
        final Double ratio = orderQueryService.getOrderRatioByLiquorId(liquorId);

        return ResponseEntity.ok(ApiResponse
                .of(OrderResultCode.ORDER_RATIO_SUCCESS, new OrderRatioResponse(ratio)));
    }

    @RequestLogging
    @PatchMapping("/cancel/{orderId}")
    public ResponseEntity<ApiResponse<Void>> cancelOrder(
            @LoginUser final Long memberId,
            @PathVariable final Long orderId
    ) {
        orderCommandService.cancelOrder(memberId, orderId);

        return ResponseEntity.ok(ApiResponse.from(OrderResultCode.ORDER_CANCEL_SUCCESS));
    }
}
