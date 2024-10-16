const std = @import("std");

pub fn main() !void {
    const args = std.process.argsWithAllocator();
    std.debug.print(args, .{});
}
