package com.nowcoder.community.entity;

//试卷-材料
public class PaperArticle {

    private int id;
    private int paperId;
    private String article; //材料内容
    private int articleOrder; //材料序号

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getPaperId() {
        return paperId;
    }

    public void setPaperId(int paperId) {
        this.paperId = paperId;
    }

    public String getArticle() {
        return article;
    }

    public void setArticle(String article) {
        this.article = article;
    }

    public int getArticleOrder() {
        return articleOrder;
    }

    public void setArticleOrder(int articleOrder) {
        this.articleOrder = articleOrder;
    }

    @Override
    public String toString() {
        return "PaperArticle{" +
                "id=" + id +
                ", paperId=" + paperId +
                ", article='" + article + '\'' +
                ", articleOrder=" + articleOrder +
                '}';
    }
}
