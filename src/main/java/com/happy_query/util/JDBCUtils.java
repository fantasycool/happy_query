package com.happy_query.util;

import com.sun.org.apache.xerces.internal.impl.dv.util.HexBin;

import javax.sql.DataSource;
import java.io.PrintStream;
import java.sql.*;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * JDBC tools to execute sql.
 * please refer to druid jdbcutil
 * https://github.com/alibaba/druid/blob/master/src/main/java/com/alibaba/druid/util/JdbcUtils.java
 * Created by frio on 16/6/15.
 */
public abstract class JDBCUtils {

    /**
     * execute query, return list result
     * @param dataSource
     * @param sql
     * @param parameters query parameters to be set
     * @return
     */
    public static List<Map<String, Object>> executeQuery(DataSource dataSource, String sql, List<Object> parameters)throws SQLException{
        Connection connection = null;
        List<Map<String, Object>> rows = new ArrayList<Map<String, Object>>();
        PreparedStatement stmt = null;
        ResultSet rs = null;
        try{
            connection = dataSource.getConnection();
            stmt = connection.prepareStatement(sql);
            setParameters(stmt, parameters);
            if(null != parameters){
                rs = stmt.executeQuery();
            }
            ResultSetMetaData rsMeta = rs.getMetaData();
            while(rs.next()){
                Map<String, Object> row = new LinkedHashMap<String, Object>();
                for (int i = 0, size = rsMeta.getColumnCount(); i < size; ++i){
                    String columnName = rsMeta.getColumnLabel(i + 1);
                    Object value = rs.getObject(i + 1);
                    row.put(columnName, value);
                }
                rows.add(row);
            }
        }finally {
            close(rs);
            close(stmt);
            close(connection);
        }
        return rows;
    }

    private static void setParameters(PreparedStatement stmt, List<Object> parameters) throws SQLException {
        for (int i = 0, size = parameters.size(); i < size; ++i) {
            Object param = parameters.get(i);
            stmt.setObject(i + 1, param);
        }
    }

    public static void close(Connection x) {
        if (x == null) {
            return;
        }
        try {
            x.close();
        } catch (Exception e) {
            throw new QueryException("close sql connection failed!", e);
        }
    }

    public static void close(Statement x) {
        if (x == null) {
            return;
        }
        try {
            x.close();
        } catch (Exception e) {
            throw new QueryException("close sql statement exception failed", e);
        }
    }

    public static void close(ResultSet x) {
        if (x == null) {
            return;
        }
        try {
            x.close();
        } catch (Exception e) {
            throw new QueryException("close result set failed", e);
        }
    }

    public static void printResultSet(ResultSet rs, PrintStream out, boolean printHeader, String seperator) throws SQLException {
        ResultSetMetaData metadata = rs.getMetaData();
        int columnCount = metadata.getColumnCount();
        if (printHeader) {
            for (int columnIndex = 1; columnIndex <= columnCount; ++columnIndex) {
                if (columnIndex != 1) {
                    out.print(seperator);
                }
                out.print(metadata.getColumnName(columnIndex));
            }
        }

        out.println();

        while (rs.next()) {

            for (int columnIndex = 1; columnIndex <= columnCount; ++columnIndex) {
                if (columnIndex != 1) {
                    out.print(seperator);
                }

                int type = metadata.getColumnType(columnIndex);

                if (type == Types.VARCHAR || type == Types.CHAR || type == Types.NVARCHAR || type == Types.NCHAR) {
                    out.print(rs.getString(columnIndex));
                } else if (type == Types.DATE) {
                    Date date = rs.getDate(columnIndex);
                    if (rs.wasNull()) {
                        out.print("null");
                    } else {
                        out.print(date.toString());
                    }
                } else if (type == Types.BIT) {
                    boolean value = rs.getBoolean(columnIndex);
                    if (rs.wasNull()) {
                        out.print("null");
                    } else {
                        out.print(Boolean.toString(value));
                    }
                } else if (type == Types.BOOLEAN) {
                    boolean value = rs.getBoolean(columnIndex);
                    if (rs.wasNull()) {
                        out.print("null");
                    } else {
                        out.print(Boolean.toString(value));
                    }
                } else if (type == Types.TINYINT) {
                    byte value = rs.getByte(columnIndex);
                    if (rs.wasNull()) {
                        out.print("null");
                    } else {
                        out.print(Byte.toString(value));
                    }
                } else if (type == Types.SMALLINT) {
                    short value = rs.getShort(columnIndex);
                    if (rs.wasNull()) {
                        out.print("null");
                    } else {
                        out.print(Short.toString(value));
                    }
                } else if (type == Types.INTEGER) {
                    int value = rs.getInt(columnIndex);
                    if (rs.wasNull()) {
                        out.print("null");
                    } else {
                        out.print(Integer.toString(value));
                    }
                } else if (type == Types.BIGINT) {
                    long value = rs.getLong(columnIndex);
                    if (rs.wasNull()) {
                        out.print("null");
                    } else {
                        out.print(Long.toString(value));
                    }
                } else if (type == Types.TIMESTAMP) {
                    out.print(String.valueOf(rs.getTimestamp(columnIndex)));
                } else if (type == Types.DECIMAL) {
                    out.print(String.valueOf(rs.getBigDecimal(columnIndex)));
                } else if (type == Types.CLOB) {
                    out.print(String.valueOf(rs.getString(columnIndex)));
                } else if (type == Types.JAVA_OBJECT) {
                    Object object = rs.getObject(columnIndex);

                    if (rs.wasNull()) {
                        out.print("null");
                    } else {
                        out.print(String.valueOf(object));
                    }
                } else if (type == Types.LONGVARCHAR) {
                    Object object = rs.getString(columnIndex);

                    if (rs.wasNull()) {
                        out.print("null");
                    } else {
                        out.print(String.valueOf(object));
                    }
                } else if (type == Types.NULL) {
                    out.print("null");
                } else {
                    Object object = rs.getObject(columnIndex);

                    if (rs.wasNull()) {
                        out.print("null");
                    } else {
                        if (object instanceof byte[]) {
                            byte[] bytes = (byte[]) object;
                            String text = HexBin.encode(bytes);
                            out.print(text);
                        } else {
                            out.print(String.valueOf(object));
                        }
                    }
                }
            }
            out.println();
        }
    }

    public static String getTypeName(int sqlType) {
        switch (sqlType) {
            case Types.ARRAY:
                return "ARRAY";

            case Types.BIGINT:
                return "BIGINT";

            case Types.BINARY:
                return "BINARY";

            case Types.BIT:
                return "BIT";

            case Types.BLOB:
                return "BLOB";

            case Types.BOOLEAN:
                return "BOOLEAN";

            case Types.CHAR:
                return "CHAR";

            case Types.CLOB:
                return "CLOB";

            case Types.DATALINK:
                return "DATALINK";

            case Types.DATE:
                return "DATE";

            case Types.DECIMAL:
                return "DECIMAL";

            case Types.DISTINCT:
                return "DISTINCT";

            case Types.DOUBLE:
                return "DOUBLE";

            case Types.FLOAT:
                return "FLOAT";

            case Types.INTEGER:
                return "INTEGER";

            case Types.JAVA_OBJECT:
                return "JAVA_OBJECT";

            case Types.LONGNVARCHAR:
                return "LONGNVARCHAR";

            case Types.LONGVARBINARY:
                return "LONGVARBINARY";

            case Types.NCHAR:
                return "NCHAR";

            case Types.NCLOB:
                return "NCLOB";

            case Types.NULL:
                return "NULL";

            case Types.NUMERIC:
                return "NUMERIC";

            case Types.NVARCHAR:
                return "NVARCHAR";

            case Types.REAL:
                return "REAL";

            case Types.REF:
                return "REF";

            case Types.ROWID:
                return "ROWID";

            case Types.SMALLINT:
                return "SMALLINT";

            case Types.SQLXML:
                return "SQLXML";

            case Types.STRUCT:
                return "STRUCT";

            case Types.TIME:
                return "TIME";

            case Types.TIMESTAMP:
                return "TIMESTAMP";

            case Types.TINYINT:
                return "TINYINT";

            case Types.VARBINARY:
                return "VARBINARY";

            case Types.VARCHAR:
                return "VARCHAR";

            default:
                return "OTHER";

        }
    }

}