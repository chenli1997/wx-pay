1、使用须知：
	1) 需实现 com.ugmro.wxpay.constant.WXPayConfig 接口
	2) 传入 WXPayConfig 到构造函数中，定义WXPayConfig对象
	3) WXPayConfig 对象创建成功后，才能进行调用对应的方法，进行微信下单


2、JSAPI 开发
	1）调用 tradePrecreate_JSAPI 方法获取到 prepay_id
	2) 调用 WxStringUtils 中的 jsApi_Sign 生成前端需要参数


3、JSAPI 前端开发(https://pay.weixin.qq.com/wiki/doc/api/jsapi.php?chapter=7_7&index=6)
	if (typeof WeixinJSBridge == "undefined"){
	   if( document.addEventListener ){
	       document.addEventListener('WeixinJSBridgeReady', onBridgeReady, false);
	   }else if (document.attachEvent){
	       document.attachEvent('WeixinJSBridgeReady', onBridgeReady); 
	       document.attachEvent('onWeixinJSBridgeReady', onBridgeReady);
	   }
	}else{
	   onBridgeReady();
	}

	function onBridgeReady(){
	   WeixinJSBridge.invoke(
	      'getBrandWCPayRequest', {
	         "appId":"wx2421b1c4370ec43b",     //公众号名称，由商户传入     
	         "timeStamp":"1395712654",         //时间戳    
	         "nonceStr":"e61463f8efa94090b1f366cccfbbb444", //随机字符串
	         "package":"prepay_id=u802345jgfjsdfgsdg888",     
	         "signType":"MD5",         //微信签名方式;
	         "paySign":"70EA570631E4BB79628FBCA90534C63FF7FADD89" //微信签名 
	      },
	      function(res){
	      if(res.err_msg == "get_brand_wcpay_request:ok" ){
	        //res.err_msg将在用户支付成功后返回ok，但并不保证它绝对可靠。
	      } 
	   }); 
	}
