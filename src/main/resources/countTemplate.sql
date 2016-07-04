SELECT
  count(*) as count_num
from
(
  SELECT
    *
  FROM
    ${left_table}
  <#if left_operation_str ??>
  where
    ${left_operation_str}
  </#if>

) a
${connect_type} join
(
	select
        bb.left_id as left_id,
        group_concat(concat(aa.dd_ref_id, ':::', aa.int_value) separator '|||') as int_strs,
        group_concat(concat(aa.dd_ref_id, ':::', aa.str_value) separator '|||') as varchar_strs,
        group_concat(concat(aa.dd_ref_id, ':::', aa.double_value) separator '|||') as double_strs,
        group_concat(concat(aa.dd_ref_id, ':::', aa.feature) separator '|||') as feature_strs
  from
    data_definition_value bb
	where
		${operation_str}
	group BY
	  bb.left_id
)b
on
	a.${primary_id}=b.left_id