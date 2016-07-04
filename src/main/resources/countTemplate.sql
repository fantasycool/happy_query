SELECT
  count(*) as count_num
from
(
  SELECT
    ${primary_id}
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
      bb.left_id
  from
    data_definition_value bb
	where
		${operation_str}
	group by
	  bb.left_id
)b
on
	a.${primary_id}=b.left_id