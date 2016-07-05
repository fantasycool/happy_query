<#if only_right ??>
  select
     count(distinct aa.left_id) as count_num
  from
    data_definition_value aa
	${join_str}
<#elseif only_left ??>
    SELECT
      count(*) as count_num
    FROM
      ${left_table}
    <#if left_operation_str ??>
    where
      ${left_operation_str}
    </#if>
<#else>
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
        aa.left_id
    from
      data_definition_value aa
    ${join_str}
    group by
        aa.left_id
  )b
  on
    a.${primary_id}=b.left_id
</#if>
