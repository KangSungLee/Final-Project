package com.example.ft.dao;

import java.util.List; 

import org.apache.ibatis.annotations.Insert; 
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Options;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

import com.example.ft.entity.Order;

import com.example.ft.entity.OrderItem;

/*
 *  구매하기 클릭 시
 *  1) 배송지: 로그인한 내 정보 userInfo에서 가져오기, 배송 요청사항, 주소는 업데이트 가능하게, 주소 저장기능(?)
 *  
 *  2) 상품정보: 상품 이름, 제조사, 옵션, 가격 등의 정보 + 총 주문금액 
 *  
 *  3) 결제: 카드결제 (일반,간편)
 */

@Mapper
public interface OrderDao {
	
	/*
	 * order
	 */
	// 
	
	// 주문한 것들 조회
	@Select("SELECT * FROM `order` WHERE email=#{email} AND isDeleted=0 ORDER BY regDate DESC")
	List<Order> getOrderListByEmail(String email);
	
	// 주문 1개 가져오기
	@Select("select * from `order` where oid=#{oid} and isDeleted=0")
	Order getOrderByOid(int oid);
	
	// 주문 생성(orderId 추가)
	@Insert(" INSERT INTO `order` (email, status, name, postCode, addr, detailAddr, tel, req, "
			+ " way, totalPrice, regDate, isDeleted, orderId) " +
	        " VALUES (#{email}, default, #{name}, #{postCode}, #{addr}, #{detailAddr}, #{tel}, #{req}, "
	        + " default, #{totalPrice}, default, default, #{orderId})")	
	@Options(useGeneratedKeys = true, keyProperty = "oid") // 생성된 키 반환
	void insertOrder(Order order);
	//
	
	// 주문 삭제
	@Update("update `order` set isDeleted = 1 where oid = #{oid}")
		void deleteOrder(int oid);
			
	/*
	 * orderItem
	 */
	
	// 주문 정보의 아이템 부분을 불러오기 위한 것으로 연결된 fk를 이용해 다른 것들에 접근 및 필요한 거 띄움
	@Select("SELECT item.name, item.price, item.salePrice, item.img1, itemOption.option" 
			+  " FROM orderItem"
			+  " JOIN item ON orderItem.iid = item.iid"
			+  " JOIN itemOption ON orderItem.ioid = itemOption.ioid"
			+  " WHERE oiid=#{oiid} and orderItem.isDeleted = 0")
	List<OrderItem> getOrderItemListByOiid(int Oiid);
	
	// 주문 아이템 생성 및 order의 oid를 사용하여 삽입
	@Insert(" INSERT INTO orderItem (oid, iid, ioid, count, price, isDeleted ) " +
	        " VALUES (#{oid}, #{iid}, #{ioid}, #{count}, #{price}, default)")
	void insertOrderItemWithOid(OrderItem orderItem); // 수정된 부분
	
	// 오더ID로 찾아서 status변경
	@Select("update `order` set status=#{status} where orderId=#{orderId}")
	void statusCheckUpdate(String status, String orderId);
	//
}
