select
	count(b.left_id) as count_num
from
	${left_table}
right join
(
	select
	      left_id,
        group_concat(concat(dd_ref_id, ':::', int_value) separator '|||') as int_strs,
        group_concat(concat(dd_ref_id, ':::', str_value) separator '|||') as varchar_strs,
        group_concat(concat(dd_ref_id, ':::', double_value) separator '|||') as double_strs
	from
		${right_table}
	where
		${operation_str}
	group BY
	  left_id
)b
on
	${left_table}.${primary_id}=b.left_id
<#if left_operation_str ??>
where
	${left_operation_str}
</#if>