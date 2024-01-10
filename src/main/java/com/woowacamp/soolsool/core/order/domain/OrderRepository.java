package com.woowacamp.soolsool.core.order.domain;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface OrderRepository extends JpaRepository<Order, Long> {

    @Query("select o from Order o join fetch o.receipt r join fetch r.receiptItems ri"
            + " where o.id = :orderId")
    Optional<Order> findOrderById(@Param("orderId") final Long orderId);

    @Query(value = "select cast(count(o.id) as double) / nullif((select count(sub_ri.id) "
            + "                                                      from receipt_items sub_ri "
            + "                                                      where sub_ri.liquor_id = :liquorId"
            + "                                                      ), 0) "
            + "from orders o inner join receipt_items ri on o.receipt_id = ri.receipt_id "
            + "where ri.liquor_id = :liquorId"
            + "      and o.order_status_id = (select os.id from order_status os where os.name = 'COMPLETED')",
            nativeQuery = true
    )
    Optional<Double> findOrderRatioByLiquorId(final Long liquorId);
}
