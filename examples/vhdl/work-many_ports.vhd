library ieee;
use ieee.std_logic_1164.all;

entity many_ports is
    port (signal in0 : in std_logic;
        signal in1 : in std_logic;
        signal in2 : in std_logic;
        signal in3 : in std_logic;
        signal in4 : in std_logic;
        signal in5 : in std_logic;
        signal in6 : in std_logic;
        signal in7 : in std_logic;
        signal in8 : in std_logic;
        signal in9 : in std_logic;
        signal in10 : in std_logic;
        signal in11 : in std_logic;
        signal in12 : in std_logic;
        signal in13 : in std_logic;
        signal in14 : in std_logic;
        signal in15 : in std_logic;
        signal in16 : in std_logic;
        signal in17 : in std_logic;
        signal in18 : in std_logic;
        signal in19 : in std_logic;
        signal in20 : in std_logic;
        signal in21 : in std_logic;
        signal in22 : in std_logic;
        signal in23 : in std_logic;
        signal in24 : in std_logic;
        signal in25 : in std_logic;
        signal in26 : in std_logic;
        signal extra_in : in std_logic;
        signal extra_out : out std_logic);
end;

architecture behavioral of many_ports is
begin
    extra_out <= extra_in;
end;

