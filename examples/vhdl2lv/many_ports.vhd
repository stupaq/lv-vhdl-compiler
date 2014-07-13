library ieee;
use ieee.std_logic_1164.all;

entity many_ports is
  port(
    in0 : in std_logic;
    in1 : in std_logic;
    in2 : in std_logic;
    in3 : in std_logic;
    in4 : in std_logic;
    in5 : in std_logic;
    in6 : in std_logic;
    in7 : in std_logic;
    in8 : in std_logic;
    in9 : in std_logic;
    in10 : in std_logic;
    in11 : in std_logic;
    in12 : in std_logic;
    in13 : in std_logic;
    in14 : in std_logic;
    in15 : in std_logic;
    in16 : in std_logic;
    in17 : in std_logic;
    in18 : in std_logic;
    in19 : in std_logic;
    in20 : in std_logic;
    in21 : in std_logic;
    in22 : in std_logic;
    in23 : in std_logic;
    in24 : in std_logic;
    in25 : in std_logic;
    in26 : in std_logic;
    extra_in : in std_logic;
    extra_out : out std_logic);
end entity;

architecture behavioral of many_ports is
begin
  extra_out <= extra_in;
end behavioral;

library ieee;
use ieee.std_logic_1164.all;

entity many_ports_outer is
  port(
    input    : in  std_logic;
    in0   : in  std_logic;
    out0  : out std_logic);
end entity;

architecture behavioral of many_ports_outer is
begin
  many_ports : entity work.many_ports port map(in0, in0, in0, in0, in0, in0, in0, in0,
   in0, in0, in0, in0, in0, in0, in0, in0, in0, in0, in0, in0, in0, in0, in0, in0, in0,
    in0, in0, input, out0);
end behavioral;
