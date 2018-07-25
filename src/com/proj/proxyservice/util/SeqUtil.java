package com.proj.proxyservice.util;

import org.apache.commons.lang.StringUtils;


public class SeqUtil
{
	private static SequenceUtil su = SequenceUtil.getInstance();
	private static String seqid = ConfigParamsUtil.getProp("seqid");
	
	synchronized public static String getSeq() 
	{
		if(StringUtils.isEmpty(seqid))seqid="1";
		return System.currentTimeMillis()+seqid+SequenceUtil.formatSequence(su.getSequence4File("dps"), 3); 
	}
} 