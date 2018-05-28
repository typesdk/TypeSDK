DROP DATABASE IF EXISTS `typesdk_db`;
CREATE DATABASE `typesdk_db`;
USE `typesdk_db`;

SET FOREIGN_KEY_CHECKS=0;

-- ----------------------------
-- Table structure for `sdk_order`
-- ----------------------------
CREATE TABLE `sdk_order` (
`ID`  int(20) NOT NULL AUTO_INCREMENT COMMENT '自增ID' ,
`ord_game`  char(30) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '订单关联游戏（区服）' ,
`ord_channel`  char(30) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '订单关联渠道' ,
`ord_cporder`  char(100) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '游戏内部订单号' ,
`ord_chorder`  char(100) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '渠道订单号' ,
`ord_status`  tinyint(1) NULL DEFAULT NULL COMMENT '订单状态（0：已创建，1：已回调，2,：已发货）' ,
`createtimevalue`  int(20) NULL DEFAULT NULL COMMENT '订单创建时间' ,
`updatetimevalue`  int(20) NULL DEFAULT NULL COMMENT '状态变更时间' ,
`ord_channelId`  char(100) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL ,
`ord_data`  mediumtext CHARACTER SET utf8 COLLATE utf8_general_ci NULL ,
`ord_verifyurl`  char(100) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL ,
`ord_notifyurl`  char(100) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL ,
`ord_successFlg`  char(20) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL ,
`ord_amount`  char(30) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL ,
`ord_channellaccountid`  char(255) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL ,
`goods_price`  char(20) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL ,
`goods_name`  char(20) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL ,
PRIMARY KEY (`ID`),
INDEX `idx_sdk_order_ord_cporder` (`ord_cporder`) USING BTREE 
)
ENGINE=InnoDB
DEFAULT CHARACTER SET=utf8 COLLATE=utf8_general_ci
COMMENT='订单记录表'
AUTO_INCREMENT=1

;

-- ----------------------------
-- Table structure for `sdk_user`
-- ----------------------------
CREATE TABLE `sdk_user` (
`ID`  int(20) NOT NULL AUTO_INCREMENT COMMENT '自增ID' ,
`SdkGameID`  char(30) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT 'SDK游戏ID' ,
`SdkChannelID`  char(30) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT 'SDK游戏渠道ID' ,
`SdkChannelName`  char(30) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT 'SDK游戏渠道名称' ,
`ChannelUserID`  char(255) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '游戏渠道用户ID' ,
`CreateTimeValue`  int(20) NULL DEFAULT NULL COMMENT '登入创建时间' ,
`UpdateTimeValue`  int(20) NULL DEFAULT NULL COMMENT '状态变更时间' ,
PRIMARY KEY (`ID`)
)
ENGINE=InnoDB
DEFAULT CHARACTER SET=utf8 COLLATE=utf8_general_ci
AUTO_INCREMENT=1

;

-- ----------------------------
-- Procedure structure for `p_game_order_search`
-- ----------------------------
DELIMITER ;;
CREATE DEFINER=`root`@`localhost` PROCEDURE `p_game_order_search`(IN `p_ord_cporder` char(100))
BEGIN
	SELECT *
  FROM game_order
  WHERE cporder = p_ord_cporder;
END
;;
DELIMITER ;

-- ----------------------------
-- Procedure structure for `p_sdk_cporder_select`
-- ----------------------------
DELIMITER ;;
CREATE DEFINER=`root`@`localhost` PROCEDURE `p_sdk_cporder_select`(IN `p_ord_cporder` CHAR(100))
BEGIN
	SELECT * FROM sdk_order WHERE ord_cporder = p_ord_cporder;
	
END
;;
DELIMITER ;

-- ----------------------------
-- Procedure structure for `p_sdk_login_log`
-- ----------------------------
DELIMITER ;;
CREATE DEFINER=`root`@`localhost` PROCEDURE `p_sdk_login_log`(IN `p_login_gameid` CHAR(30), IN `p_login_channelid` CHAR(30), IN `p_login_channelname` CHAR(30), IN `p_login_channeluserid` CHAR(255))
IF ISNULL(p_login_channeluserid) || LENGTH(TRIM(p_login_channeluserid)) < 1
THEN
	SELECT -1;
ELSE
	INSERT INTO sdk_user
    SET SdkGameID = p_login_gameid,
    SdkChannelID = p_login_channelid,
    SdkChannelName = p_login_channelname,
		ChannelUserID = `p_login_channeluserid` ,
    CreateTimeValue = unix_timestamp(now()),
    UpdateTimeValue = unix_timestamp(now());
END IF
;;
DELIMITER ;

-- ----------------------------
-- Procedure structure for `p_sdk_order_create`
-- ----------------------------
DELIMITER ;;
CREATE DEFINER=`root`@`localhost` PROCEDURE `p_sdk_order_create`(IN `p_ord_game` CHAR(30), IN `p_ord_channel` CHAR(30), IN `p_ord_cporder` CHAR(100), IN `p_ord_verifyurl` CHAR(100),IN `p_ord_channelId` CHAR(100),IN `p_ord_notifyurl` CHAR(100),IN `p_ord_channellaccountid` CHAR(255),IN `p_goods_price` CHAR(20),IN `p_goods_name` CHAR(20))
    NO SQL
IF ISNULL(p_ord_cporder) || LENGTH(TRIM(p_ord_cporder)) < 1
THEN
	SELECT -1;
ELSE
	INSERT INTO sdk_order
    SET ord_game = p_ord_game,
    ord_channel = p_ord_channel,
    ord_cporder = p_ord_cporder,
		ord_verifyurl = p_ord_verifyurl,
		ord_channelId = p_ord_channelId,
    ord_notifyurl =p_ord_notifyurl, 
		ord_channellaccountid =p_ord_channellaccountid, 
		goods_price =p_goods_price,
		goods_name =p_goods_name, 
    ord_chorder = '',
    ord_status = 0,
    createtimevalue = unix_timestamp(now()),
    updatetimevalue = unix_timestamp(now());
END IF
;;
DELIMITER ;

-- ----------------------------
-- Procedure structure for `p_sdk_order_searchByorder`
-- ----------------------------
DELIMITER ;;
CREATE DEFINER=`root`@`localhost` PROCEDURE `p_sdk_order_searchByorder`(IN `p_ord_cporder` char(100),in `p_ord_order` char(100),out `sdkStatus` tinyint)
BEGIN
	#declare p_status CHAR(30); 
	SELECT ord_status
  FROM sdk_order
  WHERE ord_cporder = p_ord_cporder 
	INTO sdkStatus;
	SELECT sdkStatus;

END
;;
DELIMITER ;

-- ----------------------------
-- Procedure structure for `p_sdk_order_stutas_select`
-- ----------------------------
DELIMITER ;;
CREATE DEFINER=`root`@`localhost` PROCEDURE `p_sdk_order_stutas_select`(IN `p_ord_game` char(30),IN `p_ord_startTime` int(20),IN `p_ord_endTime` int(20))
BEGIN
 (SELECT id, oi.ord_cporder AS cporder,oi.ord_chorder As 'order', oi.ord_status AS 'status', oi.ord_channelId AS channelId ,oi.ord_data AS 'dataInfo' ,oi.ord_verifyurl AS verifyurl,oi.ord_notifyurl As notifyurl
	FROM  sdk_order oi WHERE (createtimevalue BETWEEN p_ord_startTime AND p_ord_endTime) AND  oi.ord_game = p_ord_game AND oi.ord_status = '1' and oi.ord_data IS NOT NULL and oi.ord_channel<>'AppStore')
UNION ALL
(SELECT id, oi.ord_cporder AS cporder,oi.ord_chorder As 'order', oi.ord_status AS 'status', oi.ord_channelId AS channelId ,oi.ord_data AS 'dataInfo' ,oi.ord_verifyurl AS verifyurl,oi.ord_notifyurl As notifyurl 
	FROM  sdk_order oi WHERE (createtimevalue BETWEEN p_ord_startTime AND p_ord_endTime) AND  oi.ord_game = p_ord_game AND oi.ord_status = '2' and oi.ord_channel<>'AppStore') 
UNION ALL
	(SELECT id, oi.ord_cporder AS cporder,oi.ord_chorder As 'order', oi.ord_status AS 'status', oi.ord_channelId AS channelId ,oi.ord_data AS 'dataInfo' ,oi.ord_verifyurl AS verifyurl,oi.ord_notifyurl As notifyurl
	FROM  sdk_order oi WHERE (createtimevalue BETWEEN p_ord_startTime AND p_ord_endTime) AND  oi.ord_game = p_ord_game AND oi.ord_status = '4'and oi.ord_successFlg IS NULL and oi.ord_channel<>'AppStore') LIMIT 1000;
END
;;
DELIMITER ;

-- ----------------------------
-- Procedure structure for `p_sdk_order_update_status`
-- ----------------------------
DELIMITER ;;
CREATE DEFINER=`root`@`localhost` PROCEDURE `p_sdk_order_update_status`(IN `p_ord_game` CHAR(30), IN `p_ord_channel` CHAR(30), IN `p_ord_cporder` CHAR(100), IN `p_ord_chorder` CHAR(100), IN `p_ord_status` TINYINT(1),IN `p_ord_amount` CHAR(30))
    NO SQL
IF ISNULL(p_ord_cporder) || LENGTH(TRIM(p_ord_cporder)) < 1
THEN
	SELECT -1;
ELSE
	SET @IOU = 0;
    
	SELECT 1 INTO @IOU 
    FROM sdk_order 
    WHERE ord_game = p_ord_game
    AND ord_cporder = p_ord_cporder 
   	LIMIT 1;
    
	IF @IOU > 0
	THEN
        UPDATE sdk_order
        SET ord_status = p_ord_status,
        	ord_chorder = p_ord_chorder,
					ord_amount = p_ord_amount,
            updatetimevalue = unix_timestamp(now())
        WHERE ord_game = p_ord_game
        AND ord_cporder = p_ord_cporder;
    ELSE
        INSERT INTO sdk_order
        SET ord_game = p_ord_game,
        ord_channel = p_ord_channel,
        ord_cporder = p_ord_cporder,
        ord_chorder = p_ord_chorder,
        ord_status = p_ord_status,
				ord_amount = p_ord_amount,
        createtimevalue = unix_timestamp(now()),
        updatetimevalue = unix_timestamp(now());
    END IF;
END IF
;;
DELIMITER ;

-- ----------------------------
-- Procedure structure for `p_sdk_order_update_statusAndData`
-- ----------------------------
DELIMITER ;;
CREATE DEFINER=`root`@`localhost` PROCEDURE `p_sdk_order_update_statusAndData`(IN `p_ord_game` CHAR(30),IN `p_ord_channel` CHAR(30),IN `p_ord_cporder` CHAR(100),IN `p_ord_chorder` CHAR(100),IN `p_ord_status` TINYINT(1),IN `p_ord_amount` CHAR(30),IN `p_ord_data` mediumtext)
IF ISNULL(p_ord_cporder) || LENGTH(TRIM(p_ord_cporder)) < 1
THEN
	SELECT -1;
ELSE
	SET @IOU = 0;
    
	SELECT 1 INTO @IOU 
    FROM sdk_order 
    WHERE ord_game = p_ord_game
    AND ord_cporder = p_ord_cporder 

   	LIMIT 1;
    
	IF @IOU > 0
	THEN
        UPDATE sdk_order
        SET ord_status = p_ord_status,
        	ord_chorder = p_ord_chorder,
					ord_amount = p_ord_amount,
					ord_data = p_ord_data,
          updatetimevalue = unix_timestamp(now())
        WHERE ord_game = p_ord_game
        AND ord_cporder = p_ord_cporder;
				
				
    ELSE
        INSERT INTO sdk_order
        SET ord_game = p_ord_game,
        ord_channel = p_ord_channel,
        ord_cporder = p_ord_cporder,
        ord_chorder = p_ord_chorder,
        ord_status = p_ord_status,
				ord_amount = p_ord_amount,
				ord_data = p_ord_data,
        createtimevalue = unix_timestamp(now()),
        updatetimevalue = unix_timestamp(now());
    END IF;
END IF
;;
DELIMITER ;

-- ----------------------------
-- Procedure structure for `p_sdk_order_update_successFlg`
-- ----------------------------
DELIMITER ;;
CREATE DEFINER=`root`@`localhost` PROCEDURE `p_sdk_order_update_successFlg`(IN `p_ord_cporder` char(100),IN `p_ord_channelId` char(100))
IF ISNULL(p_ord_cporder) || LENGTH(TRIM(p_ord_cporder)) < 1
THEN
	SELECT -1;
ELSE
	SET @IOU = 0;
    
	SELECT 1 INTO @IOU 
    FROM sdk_order 
    WHERE ord_channelId = p_ord_channelId
    AND ord_cporder = p_ord_cporder 
   	LIMIT 1;
    
	IF @IOU > 0
	THEN
        UPDATE sdk_order
        SET ord_successFlg = '1',
        	
            updatetimevalue = unix_timestamp(now())
        WHERE ord_channelId = p_ord_channelId
        AND ord_cporder = p_ord_cporder;
   
       
    END IF;
END IF
;;
DELIMITER ;

-- ----------------------------
-- Procedure structure for `p_sdk_request_log_insert`
-- ----------------------------
DELIMITER ;;
CREATE DEFINER=`root`@`localhost` PROCEDURE `p_sdk_request_log_insert`(IN `p_req_game` CHAR(30), IN `p_req_channel` CHAR(30), IN `p_req_action` CHAR(10), IN `p_req_detail` VARCHAR(3000))
    NO SQL
    COMMENT '插入一条日志'
INSERT INTO sdk_request_log
    SET req_game = p_req_game,
    req_channel = p_req_channel,
    req_action = p_req_action,
    req_detail = p_req_detail,
    timevalue = unix_timestamp(now())
;;
DELIMITER ;

-- ----------------------------
-- Auto increment value for `sdk_order`
-- ----------------------------
ALTER TABLE `sdk_order` AUTO_INCREMENT=37489;

-- ----------------------------
-- Auto increment value for `sdk_user`
-- ----------------------------
ALTER TABLE `sdk_user` AUTO_INCREMENT=485;
