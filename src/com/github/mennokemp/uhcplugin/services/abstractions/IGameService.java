package com.github.mennokemp.uhcplugin.services.abstractions;

import com.github.mennokemp.uhcplugin.helpers.Result;

public interface IGameService extends IGameOverListener
{
	public Result StartUhc();
	
	public Result CancelUhc();
	
	public Result StopUhc();
}