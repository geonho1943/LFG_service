package com.geonho1943.LFG.model;

import com.geonho1943.LFG.extraDB.Doc;
import com.geonho1943.LFG.extraDB.User;
import org.springframework.jdbc.datasource.DataSourceUtils;

import javax.sql.DataSource;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class DocModel implements DocRepository{
    private final DataSource dataSource;
    public DocModel(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    @Override
    public Doc post(Doc doc) {
        String sql = "INSERT INTO `LFGservice`.`lfg_doc` (`doc_tit`, `doc_writ`, `doc_cont`,`doc_reg`) VALUES (?,?,?,sysdate());";
        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        try {
            conn = getConnection();
            pstmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
            pstmt.setString(1, doc.getDoc_tit());
            pstmt.setString(2, doc.getDoc_writ());
            pstmt.setString(3, doc.getDoc_cont());
            pstmt.executeUpdate();
            rs = pstmt.getGeneratedKeys();
            if (rs.next()) {
                doc.setDoc_idx(rs.getInt(1));
            } else {
                throw new SQLException("idx 조회 실패");
            }
            return doc;
        } catch (Exception e) {
            throw new IllegalStateException(e);
        } finally {
            close(conn, pstmt, rs);
        }
    }

    @Override
    public Doc read(Doc doc) {
        String sql = "select * from lfg_doc where doc_tit=?";
        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        try {
            conn = getConnection();
            pstmt = conn.prepareStatement(sql);
            pstmt.setString(1, doc.getDoc_tit());
            rs = pstmt.executeQuery();
            if (rs.next()) {
                doc.setDoc_idx(rs.getInt("doc_idx"));
                doc.setDoc_tit(rs.getString("doc_tit"));
                doc.setDoc_writ(rs.getString("doc_writ"));
                doc.setDoc_cont(rs.getString("doc_cont"));
                doc.setDoc_leg(rs.getString("doc_reg"));
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }finally {
            close(conn, pstmt, rs);
        }
        return doc;
    }

    @Override
    public List<Doc> list() {
        String sql = "select * from lfg_doc;";
        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        try {
            conn = getConnection();
            pstmt = conn.prepareStatement(sql);
            rs = pstmt.executeQuery();

            List<Doc> docs = new ArrayList<>();
            while (rs.next()) {
                Doc doc = new Doc();
                doc.setDoc_idx(rs.getInt("doc_idx"));
                doc.setDoc_tit(rs.getString("doc_tit"));
                doc.setDoc_writ(rs.getString("doc_writ"));
                doc.setDoc_cont(rs.getString("doc_cont"));
                doc.setDoc_leg(rs.getString("doc_reg"));
                docs.add(doc);
            }
            return docs;
        } catch (Exception e) {
            throw new IllegalStateException(e);
        } finally {
            close(conn, pstmt, rs);
        }
    }
    @Override
    public Doc modify(Doc doc) {
        return null;
    }

    @Override
    public Doc delete(Doc doc) {
        //DELETE FROM `LFGservice`.`lfg_doc` WHERE (`doc_idx` = '7');
        return null;
    }



    private Connection getConnection() {
        return DataSourceUtils.getConnection(dataSource);
    }
    private void close(Connection conn, PreparedStatement pstmt, ResultSet rs){
        try {
            if (rs != null) {
                rs.close();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } try {
            if (pstmt != null) {
                pstmt.close();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        try {
            if (conn != null) {
                close(conn);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
    private void close(Connection conn) throws SQLException {
        DataSourceUtils.releaseConnection(conn, dataSource);
    }
}
