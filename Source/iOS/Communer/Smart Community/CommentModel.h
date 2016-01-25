//
//  CommentModel.h
//  Smart Community
//
//  Created by oren shany on 8/6/15.
//  Copyright (c) 2015 Moveo. All rights reserved.
//

#import <Foundation/Foundation.h>
#import "KVCBaseObject.h"

@interface CommentModel : KVCBaseObject

@property (nonatomic, strong) NSString* postID ;
@property (nonatomic, strong) NSString* imageUrl ;
@property (nonatomic, strong) NSString* name;
@property (nonatomic) NSTimeInterval date;
@property (nonatomic, strong) NSString* content;
@property (nonatomic) double rate;
@property (nonatomic) BOOL isRebai;

@end
