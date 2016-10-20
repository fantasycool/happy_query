<#if only_left ??>
  SELECT
    a.*
  FROM
    ${left_table} a
  <#if left_operation_str ??>
  where
    ${left_operation_str}
  </#if>
  limit ${start_index}, ${size}
<#elseif only_right ??>
  SELECT
    a.*
  FROM
    ${left_table}
  right join
  (
    SELECT
      p.prm_id
    FROM
      data_definition_value p
    ${join_str}
    group BY
      p.prm_id
    limit ${start_index}, ${size}
  ) b
  on
    a.id = b.prm_id
<#else>
  SELECT
    a.*
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
  JOIN
  (
    SELECT
      p.prm_id
    FROM
      data_definition_value p
    ${join_str}
    group BY
      p.prm_id
  ) b
  on
    a.id = b.prm_id
  limit ${start_index}, ${size}
</#if>