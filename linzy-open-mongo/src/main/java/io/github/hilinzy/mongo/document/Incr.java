package io.github.hilinzy.mongo.document;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

/**
  _     _  __  _ _____ __  __
 | |__ | ||  \| |` / /_\ \/ /
 |____||_||_|\__| /___/ |__|
 * @description  维护mongodb中其他集合的自增id
 * @author linzy
 * @date  2022/7/26
 **/
@Document
@Data
public class Incr {

    @Id
    private String _id;

    private String collectionName;

    private Long incrId;
}