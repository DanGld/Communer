//
//  UpdateUserModel.h
//  Smart Community
//
//  Created by oren shany on 8/6/15.
//  Copyright (c) 2015 Moveo. All rights reserved.
//

#import <Foundation/Foundation.h>
#import "KVCBaseObject.h"


@interface UpdateUserModel : KVCBaseObject

@property (nonatomic, strong) NSString* userID;
@property (nonatomic, strong) NSString* name;
@property (nonatomic, strong) NSString* lastname;
@property (nonatomic, strong) NSString* gender;
@property (nonatomic, strong) NSString* mStatus;
@property (nonatomic) int bDay;
@property (nonatomic, strong) NSString* email;
@property (nonatomic, strong) NSString* imageUrl;

@end
