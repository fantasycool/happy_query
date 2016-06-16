select
	a.*,
  b.int_strs,
  b.varchar_strs,
  b.double_strs
from
	${left_table}
right join
(
	select
	      left_id,
        group_concat(concat(dd_ref_id, ':::', int_value) separator '|||') as int_strs,
        group_concat(concat(dd_ref_id, ':::', str_value) separator '|||') as vachar_strs,
        group_concat(concat(dd_ref_id, ':::', double_value) separator '|||') as double_strs
	from
		${right_table}
	where
		${operation_str}
	group BY
	  left_id
	limit ${start_index}, ${size}
)b
on
	${left_table}.${primary_id}=b.left_id
<#if left_operation_str??>
where
	$left_operation_str
</#if>