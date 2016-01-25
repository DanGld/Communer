//
//  ServiceCommentModel.h
//  Smart Community
//
//  Created by oren shany on 8/6/15.
//  Copyright (c) 2015 Moveo. All rights reserved.
//

#import <Foundation/Foundation.h>
#import "KVCBaseObject.h"

@interface ServiceCommentModel : KVCBaseObject

@property (nonatomic, strong) NSString* imageUrl;
@property (nonatomic, strong) NSString* name;
@property (nonatomic) double rate;
@property (nonatomic, strong) NSString* content;

@end
