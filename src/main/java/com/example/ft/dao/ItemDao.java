package com.example.ft.dao;

import java.util.List;

import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.SelectKey;
import org.apache.ibatis.annotations.Update;

import com.example.ft.entity.Item;
import com.example.ft.entity.ItemOption;
import com.example.ft.entity.ItemTag;

@Mapper
public interface ItemDao {
	
	@Select("select * from item where iId=#{iid} and isDeleted=0")
	Item getItemIid(int iid);

	@Select("select * from item where isDeleted=0 order by regDate desc")
	List<Item> getItemList();
	
	@Select("select * from item WHERE CONCAT(name, category, content, option, tag) LIKE ${query} AND isDeleted=0 order by regDate desc")
	List<Item> getSearchItemList(String query);
	
	@Insert("insert into item values (default, #{name}, #{category}, #{img1}, #{img2}, #{img3},"
			+ " #{content}, #{price}, default, default, default, default, default)")
	@SelectKey(statement="SELECT LAST_INSERT_ID()", keyProperty="iid", before=false, resultType=int.class)
	void insertItem(Item item);
	
	@Update("update item set name=#{name}, category=#{category}, img1=#{img1}, img2=#{img2},"
			+ " content=#{content}, price=#{price}, option=#{option}, count=#{count}, "
			+ " tag=#{tag} where iid=#{iid}")
	void updateItem(Item item);	
	
	@Update("update item set isDeleted=1 where iid=#{iid}")
	void deleteItem(int iid);
	
	@Update("update item set salePrice=#{salePrice}, saleDate=#{saleDate} where iid=#{iid}")
	void saleItem(Item item);
	
	// itemOption
	@Insert("insert into itemoption values (default, #{iid}, #{option}, #{count},default)")
	void optionInsert(ItemOption itemOption);
	
	// itemTag
	@Insert("insert into itemtag values (default, #{iid}, #{tag}, default)")
	void tagInsert(ItemTag itemTag);
}