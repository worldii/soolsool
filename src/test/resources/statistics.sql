/*
CREATE TABLE `statistics` (
  `statistic_year` int NOT NULL,
  `statistic_month` int NOT NULL,
  `statistic_week` int NOT NULL,
  `statistic_day` int NOT NULL,
  `liquor_id` bigint NOT NULL,
  `click` bigint DEFAULT NULL,
  `region` varchar(255) DEFAULT NULL,
  `brew_type` varchar(255) DEFAULT NULL,
  `impression` decimal(19,2) DEFAULT NULL,
  `sale_quantity` decimal(19,2) DEFAULT NULL,
  `sale_price` decimal(19,2) DEFAULT NULL,
  PRIMARY KEY (`statistic_year`,`statistic_month`,`statistic_week`,`statistic_day`,`liquor_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci
*/

insert into statistics(
                       statistic_year, statistic_month, statistic_week,
                       statistic_day, liquor_id, click, region, brew_type,
                       impression, sale_quantity, sale_price
                 ) values( 2023, 1, 7, 1, 1, 1, '서울', '맥주', 1, 1, 1),
                         ( 2023, 1, 7, 1, 2, 2, '서울', '맥주', 1, 1, 1),
                         ( 2023, 1, 7, 1, 3, 3, '서울', '맥주', 1, 1, 1),
                         ( 2023, 1, 7, 1, 4, 4, '서울', '맥주', 1, 1, 1),
                         ( 2023, 1, 7, 1, 5, 5, '서울', '맥주', 1, 1, 1),
                         ( 2023, 1, 7, 1, 6, 6, '서울', '맥주', 1, 1, 1),
                         ( 2023, 1, 7, 1, 7, 7, '서울', '맥주', 1, 1, 1),
                         ( 2023, 1, 7, 1, 8, 8, '서울', '맥주', 1, 1, 1),
                         ( 2023, 1, 7, 1, 9, 9, '서울', '맥주', 1, 1, 1),
                         ( 2023, 1, 7, 1, 10, 10, '서울', '맥주', 1, 1, 1);
