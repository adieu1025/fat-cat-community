package com.nowcoder.community.dao.elasticsearch;

import com.nowcoder.community.entity.DiscussPost;
import org.springframework.data.elasticsearch.repository.ElasticsearchCrudRepository;
import org.springframework.stereotype.Repository;

//声明实体，以及实体的主键类型
@Repository
public interface DiscussPostRepository extends ElasticsearchCrudRepository<DiscussPost, Integer> {

}
