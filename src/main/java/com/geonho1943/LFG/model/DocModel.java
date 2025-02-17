package com.geonho1943.LFG.model;

import com.geonho1943.LFG.dto.Doc;
import org.springframework.jdbc.datasource.DataSourceUtils;
import org.springframework.transaction.annotation.Transactional;

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
    @Transactional(readOnly = true)
    public int docCount() {// 페이징을 위해 모든 문서의 수를 반환
        int docCount = 0;
        String sql = "select count(*) as total_count from lfg_doc";
        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        try {
            conn = getConnection();
            pstmt = conn.prepareStatement(sql);
            rs = pstmt.executeQuery();
            while (rs.next()) {
                docCount = rs.getInt(1);
            }
            return docCount;
        } catch (Exception e) {
            throw new IllegalStateException(e);
        } finally {
            close(conn, pstmt, rs);
        }
    }

    @Override
    @Transactional(readOnly = true)
    public int docSearchCount(String name) {// 페이징을 위해 모든 문서의 수를 반환
        int docCount = 0;
        String sql = "select count(*) as total_count from lfg_doc where doc_app_name=?";
        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        try {
            conn = getConnection();
            pstmt = conn.prepareStatement(sql);
            pstmt.setString(1,name);
            rs = pstmt.executeQuery();
            while (rs.next()) {
                docCount = rs.getInt(1);
            }
            return docCount;
        } catch (Exception e) {
            throw new IllegalStateException(e);
        } finally {
            close(conn, pstmt, rs);
        }
    }

    @Override
    public List<Doc> myDocList(String user_name) {// 유저프로필에서 직접 작성한 글 리스트 myDocs 반환
        String sql = "select * from `LFGservice`.`lfg_doc`where doc_writ=? order by doc_idx desc";
        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        List<Doc> myDocs = new ArrayList<>();
        try {
            conn = getConnection();
            pstmt = conn.prepareStatement(sql);
            pstmt.setString(1,user_name);
            rs = pstmt.executeQuery();
            while (rs.next()) {
                Doc doc = new Doc();
                doc.setDoc_idx(rs.getInt("doc_idx"));
                doc.setDoc_sub(rs.getString("doc_sub"));
                doc.setDoc_writ(rs.getString("doc_writ"));
                doc.setDoc_cont(rs.getString("doc_cont"));
                doc.setDoc_reg(rs.getString("doc_reg"));
                doc.setDoc_app_id(rs.getInt("doc_app_id"));
                doc.setDoc_app_name(rs.getString("doc_app_name"));
                myDocs.add(doc);
            }
            return myDocs;
        } catch (Exception e) {
            throw new IllegalStateException(e);
        } finally {
            close(conn, pstmt, rs);
        }
    }


    @Override
    public Doc post(Doc doc) {
        String sql = "INSERT INTO `LFGservice`.`lfg_doc` (`doc_sub`, `doc_writ`, `doc_cont`,`doc_app_id`,`doc_app_name`,`doc_reg`) VALUES (?,?,?,?,?,CURRENT_TIMESTAMP())";
        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        try {
            conn = getConnection();
            pstmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
            pstmt.setString(1, doc.getDoc_sub());
            pstmt.setString(2, doc.getDoc_writ());
            pstmt.setString(3, doc.getDoc_cont());
            pstmt.setInt(4, doc.getDoc_app_id());
            pstmt.setString(5, doc.getDoc_app_name());
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
        String sql = "select * from `LFGservice`.`lfg_doc` where doc_idx=?";
        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        try {
            conn = getConnection();
            pstmt = conn.prepareStatement(sql);
            pstmt.setInt(1,doc.getDoc_idx());
            rs = pstmt.executeQuery();
            if (rs.next()) {
                doc.setDoc_sub(rs.getString("doc_sub"));
                doc.setDoc_writ(rs.getString("doc_writ"));
                doc.setDoc_cont(rs.getString("doc_cont"));
                doc.setDoc_reg(rs.getString("doc_reg"));
                doc.setDoc_app_id(rs.getInt("doc_app_id"));
                doc.setDoc_app_name(rs.getString("doc_app_name"));
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }finally {
            close(conn, pstmt, rs);
        }
        return doc;
    }

    @Override
    public List<Doc> list(int docStart) {
        String sql = "select * from `LFGservice`.`lfg_doc` order by doc_idx desc limit ?,5";
        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        try {
            conn = getConnection();
            pstmt = conn.prepareStatement(sql);
            pstmt.setInt(1,docStart);
            rs = pstmt.executeQuery();
            List<Doc> docs = new ArrayList<>();
            while (rs.next()) {
                Doc doc = new Doc();
                doc.setDoc_idx(rs.getInt("doc_idx"));
                doc.setDoc_sub(rs.getString("doc_sub"));
                doc.setDoc_writ(rs.getString("doc_writ"));
                doc.setDoc_cont(rs.getString("doc_cont"));
                doc.setDoc_reg(rs.getString("doc_reg"));
                doc.setDoc_app_id(rs.getInt("doc_app_id"));
                doc.setDoc_app_name(rs.getString("doc_app_name"));
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
        String sql = "update `LFGservice`.`lfg_doc` set doc_sub = ?,doc_cont = ?,doc_app_id = ?,doc_app_name = ? where doc_idx = ?";
        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        try {
            conn = getConnection();
            pstmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
            pstmt.setString(1, doc.getDoc_sub());
            pstmt.setString(2, doc.getDoc_cont());
            pstmt.setInt(3,doc.getDoc_app_id());
            pstmt.setString(4,doc.getDoc_app_name());
            pstmt.setInt(5, doc.getDoc_idx());
            pstmt.executeUpdate();
            rs = pstmt.getGeneratedKeys();
            return doc;
        } catch (Exception e) {
            throw new IllegalStateException(e);
        } finally {
            close(conn, pstmt, rs);
        }
    }

    @Override
    public void delete(Doc doc) {
        String sql = "DELETE FROM `LFGservice`.`lfg_doc` WHERE (`doc_idx` = ?)";
        Connection conn = null;
        PreparedStatement pstmt = null;
        try {
            conn = getConnection();
            pstmt = conn.prepareStatement(sql);
            pstmt.setInt(1, doc.getDoc_idx());
            pstmt.executeUpdate();
        } catch (Exception e) {
            throw new IllegalStateException(e);
        } finally {
            close(conn, pstmt, null);
        }
    }

    @Override
    public List<Doc> appNameList(Doc doc, int docStart) {
        String sql = "select * from `LFGservice`.`lfg_doc` where doc_app_name=? order by doc_idx desc limit ?,5";
        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        try {
            conn = getConnection();
            pstmt = conn.prepareStatement(sql);
            pstmt.setString(1,doc.getDoc_app_name());
            pstmt.setInt(2,docStart);
            rs = pstmt.executeQuery();
            List<Doc> docs = new ArrayList<>();
            while (rs.next()) {
                doc = new Doc();
                doc.setDoc_idx(rs.getInt("doc_idx"));
                doc.setDoc_sub(rs.getString("doc_sub"));
                doc.setDoc_writ(rs.getString("doc_writ"));
                doc.setDoc_cont(rs.getString("doc_cont"));
                doc.setDoc_reg(rs.getString("doc_reg"));
                doc.setDoc_app_id(rs.getInt("doc_app_id"));
                doc.setDoc_app_name(rs.getString("doc_app_name"));
                docs.add(doc);
            }
            return docs;
        } catch (Exception e) {
            throw new IllegalStateException(e);
        } finally {
            close(conn, pstmt, rs);
        }
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
