package com.beibeilian.circle.mvp.presenter;

import com.beibeilian.circle.bean.User;
import com.beibeilian.circle.mvp.modle.CircleModel;
import com.beibeilian.circle.mvp.modle.IDataRequestListener;
import com.beibeilian.circle.mvp.view.ICircleViewUpdate;

public class CirclePresenter {
	private CircleModel mCircleModel;
	private ICircleViewUpdate mCircleView;
	
	public CirclePresenter(ICircleViewUpdate view){
		this.mCircleView = view;
		mCircleModel = new CircleModel();
	}
	/**
	 * 
	* @Title: deleteCircle 
	* @Description: 删除动�? 
	* @param  circleId     
	* @return void    返回类型 
	* @throws
	 */
	public void deleteCircle(final String circleId){
		mCircleModel.deleteCircle(new IDataRequestListener() {
			
			@Override
			public void loadSuccess(Object object) {
				mCircleView.update2DeleteCircle(circleId);
			}
		});
	}
	/**
	 * 
	* @Title: addFavort 
	* @Description: 点赞
	* @param  circlePosition     
	* @return void    返回类型 
	* @throws
	 */
	public void addFavort(final int circlePosition,final String id){
		mCircleModel.addFavort(new IDataRequestListener() {
			
			@Override
			public void loadSuccess(Object object) {
				mCircleView.update2AddFavorite(circlePosition,id);
			}
		});
	}
	/**
	 * 
	* @Title: deleteFavort 
	* @Description: 取消点赞 
	* @param @param circlePosition
	* @param @param favortId     
	* @return void    返回类型 
	* @throws
	 */
	public void deleteFavort(final int circlePosition, final String favortId){
		mCircleModel.deleteFavort(new IDataRequestListener() {
				
				@Override
				public void loadSuccess(Object object) {
					mCircleView.update2DeleteFavort(circlePosition, favortId);
				}
			});
	}
	
	/**
	 * 
	* @Title: addComment 
	* @Description: 增加评论
	* @param  circlePosition
	* @param  type  0：发布评�? 1：回复评�?	* @param  replyUser  回复评论时对谁的回复   
	* @return void    返回类型 
	* @throws
	 */
	public void addComment(final int circlePosition, final int type, final User replyUser,final String id){
		mCircleModel.addComment(new IDataRequestListener(){

			@Override
			public void loadSuccess(Object object) {
				mCircleView.update2AddComment(circlePosition, type, replyUser,id);
			}
			
		});
	}
	
	/**
	 * 
	* @Title: deleteComment 
	* @Description: 删除评论 
	* @param @param circlePosition
	* @param @param commentId     
	* @return void    返回类型 
	* @throws
	 */
	public void deleteComment(final int circlePosition, final String commentId){
		mCircleModel.addComment(new IDataRequestListener(){

			@Override
			public void loadSuccess(Object object) {
				mCircleView.update2DeleteComment(circlePosition, commentId);
			}
			
		});
	}
}
