//
//  CommunityMemberModel.h
//  Smart Community
//
//  Created by oren shany on 8/6/15.
//  Copyright (c) 2015 Moveo. All rights reserved.
//

#import <Foundation/Foundation.h>
#import "KVCBaseObject.h"

@interface CommunityMemberModel : KVCBaseObject

@property (nonatomic, strong) NSString* imageUrl ;
@property (nonatomic, strong) NSString* name ;
@property (nonatomic, strong) NSString* phone;

@end
