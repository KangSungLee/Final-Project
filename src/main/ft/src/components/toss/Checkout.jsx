import React, { useEffect, useRef, useState } from "react";
import { useLocation, useNavigate } from "react-router-dom";
import { loadPaymentWidget } from "@tosspayments/payment-widget-sdk";
import { Button, Card, CardContent } from "@mui/material";
import axios from "axios";

const widgetClientKey = process.env.REACT_APP_WIDGET_CLIENT_KEY;
const customerKey = process.env.REACT_APP_CUSTOMER_KEY;

export function CheckoutPage() {
  const [paymentWidget, setPaymentWidget] = useState(null);
  const paymentMethodsWidgetRef = useRef(null);
  const [price, setPrice] = useState(0); 
  const location = useLocation();
  const { orderData } = location.state || {};
 console.log(orderData.order.orderId);
  useEffect(() => {
    const fetchPaymentWidget = async () => {
      try {
        const loadedWidget = await loadPaymentWidget(widgetClientKey, customerKey);
        setPaymentWidget(loadedWidget);
      } catch (error) {
        console.error("Error fetching payment widget:", error);
      }
    };

    fetchPaymentWidget();
  }, []);

  useEffect(() => {
    if (paymentWidget == null || !orderData) {
      return;
    }

    // 주문 총 금액을 계산하여 설정
    const totalPrice = orderData.order.totalPrice || 0;
    setPrice(totalPrice);

    const paymentMethodsWidget = paymentWidget.renderPaymentMethods(
      "#payment-widget",
      { value: totalPrice },
      { variantKey: "DEFAULT" }
    );

    paymentWidget.renderAgreement(
      "#agreement", 
      { variantKey: "AGREEMENT" }
    );

    paymentMethodsWidgetRef.current = paymentMethodsWidget;
  }, [paymentWidget, orderData]);

  useEffect(() => {
    const paymentMethodsWidget = paymentMethodsWidgetRef.current;

    if (paymentMethodsWidget == null) {
      return;
    }

    paymentMethodsWidget.updateAmount(price);
  }, [price]);
  
  const handlePaymentRequest = async () => {
    try {
      const response = await axios.post('/ft/order/insert', orderData);
      console.log(response);
      // 주문 정보를 이용하여 결제 요청을 보냄
      await paymentWidget?.requestPayment({
        orderId: orderData.order.orderId,
        orderName: `${orderData.orderItems.length > 1 ? '외 ' + (orderData.orderItems.length - 1) + ', ' : ''}${orderData.orderItems[0].name}`, // 주문명을 설정
        customerName: orderData.order.name || "", // 주문자 이름 설정
        customerEmail: orderData.order.email || "", // 주문자 이메일 설정
        customerMobilePhone: orderData.order.tel ? orderData.order.tel.replace(/-/g, '') : "",// 주문자 전화번호 설정
        successUrl: `${window.location.origin}/success`,
        failUrl: `${window.location.origin}/fail`,
      });
      
      
      // 결제 성공 후 /success 페이지로 이동, orderData도 함께 전달
    } catch (error) {
      console.error("Error requesting payment:", error);
    }
  };

  return (
    <div style={{ padding: 50, textAlign: 'center' }}>
      <Card>
        <CardContent>
          <div id="payment-widget" />
          <div id="agreement" />
          <div style={{ marginTop: 20 }}>
            <Button variant="contained" style={{ backgroundColor: 'grey' }} onClick={handlePaymentRequest}>
              결제하기
            </Button>
          </div>
        </CardContent>
      </Card>
    </div>
  );
}
