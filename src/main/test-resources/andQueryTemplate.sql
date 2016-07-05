<#if only_right ??>
  SELECT
      a.*,
      b.left_id,
      b.int_strs,
      b.varchar_strs,
      b.double_strs,
      b.feature_strs
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
    right join
    (
      select
            aa.left_id as left_id,
            group_concat(concat(aa.dd_ref_id, ':::', aa.int_value) separator '|||') as int_strs,
            group_concat(concat(aa.dd_ref_id, ':::', aa.str_value) separator '|||') as varchar_strs,
            group_concat(concat(aa.dd_ref_id, ':::', aa.double_value) separator '|||') as double_strs,
            group_concat(concat(aa.dd_ref_id, ':::', aa.feature) separator '|||') as feature_strs
      from
        data_definition_value aa
      ${join_str}
      group BY
        aa.left_id
      limit ${start_index}, ${size}
    )b
    on
      a.${primary_id}=b.left_id
<#elseif only_left ??>
    SELECT
      a.*,
      a.${primary_id} as left_id,
      b.int_strs,
      b.varchar_strs,
      b.double_strs,
      b.feature_strs
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
      limit ${start_index}, ${size}
    ) a
    left join
    (
      select
            aa.left_id as left_id,
            group_concat(concat(aa.dd_ref_id, ':::', aa.int_value) separator '|||') as int_strs,
            group_concat(concat(aa.dd_ref_id, ':::', aa.str_value) separator '|||') as varchar_strs,
            group_concat(concat(aa.dd_ref_id, ':::', aa.double_value) separator '|||') as double_strs,
            group_concat(concat(aa.dd_ref_id, ':::', aa.feature) separator '|||') as feature_strs
      from
        (
          select
          z.*
          from
          data_definition_value z
          right join
          (
            SELECT
              *
            FROM
              ${left_table}
            <#if left_operation_str ??>
            where
              ${left_operation_str}
            </#if>
            limit ${start_index}, ${size}
          ) y
          on
          z.left_id = y.${primary_id}
        ) aa
      ${join_str}
      group BY
        aa.left_id
    )b
    on
      a.${primary_id}=b.left_id
<#else>
    SELECT
      a.*,
      a.${primary_id} as left_id,
      b.int_strs,
      b.varchar_strs,
      b.double_strs,
      b.feature_strs
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
            aa.left_id as left_id,
            group_concat(concat(aa.dd_ref_id, ':::', aa.int_value) separator '|||') as int_strs,
            group_concat(concat(aa.dd_ref_id, ':::', aa.str_value) separator '|||') as varchar_strs,
            group_concat(concat(aa.dd_ref_id, ':::', aa.double_value) separator '|||') as double_strs,
            group_concat(concat(aa.dd_ref_id, ':::', aa.feature) separator '|||') as feature_strs
      from
      (
          select
          z.*
          from
          data_definition_value z
          right join
          (
            SELECT
              ${primary_id}
            FROM
              ${left_table}
            <#if left_operation_str ??>
            where
              ${left_operation_str}
            </#if>
          ) y
          on
          z.left_id = y.${primary_id}
      ) aa
      ${join_str}
      group BY
        aa.left_id
    )b
    on
      a.${primary_id}=b.left_id
    limit ${start_index}, ${size}
</#if>