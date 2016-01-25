//
//  postModel.h
//  Smart Community
//
//  Created by oren shany on 8/6/15.
//  Copyright (c) 2015 Moveo. All rights reserved.
//

#import <Foundation/Foundation.h>
#import "CommunityMemberModel.h"
#import "KVCBaseObject.h"

@interface postModel : KVCBaseObject

@property (nonatomic, strong) NSString* postID ;
@property (nonatomic, strong) CommunityMemberModel* communityMember ;
@property (nonatomic, strong) NSString* title;
@property (nonatomic, strong) NSString* imageUrl;
@property (nonatomic) NSTimeInterval cDate ;
@property (nonatomic, strong) NSString* content ;
@property (nonatomic, strong) NSMutableArray* comments ;
@property (nonatomic) BOOL isPublic;



@end
