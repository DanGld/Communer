//
//  JoinCommunityModel.h
//  Smart Community
//
//  Created by oren shany on 8/6/15.
//  Copyright (c) 2015 Moveo. All rights reserved.
//

#import <Foundation/Foundation.h>
#import "KVCBaseObject.h"

@interface JoinCommunityModel : KVCBaseObject

@property (nonatomic, strong) NSString* communityID;
@property (nonatomic) BOOL asGuest;

@end
