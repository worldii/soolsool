package com.woowacamp.soolsool.core.order.domain;

import com.woowacamp.soolsool.core.order.domain.vo.OrderStatusType;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface OrderStatusRepository extends JpaRepository<OrderStatus, Long> {

    Optional<OrderStatus> findByType(final OrderStatusType type);
}
