library ieee;
use ieee.std_logic_1164.all;

entity branch_and_merge is
end;

architecture behavioral of branch_and_merge is
    component sink is
        generic (n : integer := 4);
        port (input : in std_logic_vector(n downto 0));
    end component;
    component source is
        generic (n : integer := 4);
        port (output : out std_logic_vector(n downto 0));
    end component;
    signal aaa : std_logic_vector(7 downto 0);
    signal bbb : std_logic_vector(7 downto 0);
    signal ccc : std_logic_vector(7 downto 0);
    signal ddd : std_logic_vector(7 downto 0);
begin
    dst0 : component sink
        port map(input => aaa(3 downto 0));
    dst1 : component sink
        port map(input => aaa(7 downto 4));
    dst2 : component sink
        generic map(n => 7)
        port map(input => bbb);
    dst3 : component sink
        port map(input => ccc(3 downto 0));
    dst4 : component sink
        port map(input => ccc(7 downto 4));
    dst5 : component sink
        generic map(n => 7)
        port map(input => ddd);
    src0 : component source
        port map(output => aaa(3 downto 0));
    src1 : component source
        port map(output => aaa(7 downto 4));
    src2 : component source
        port map(output => bbb(3 downto 0));
    src3 : component source
        port map(output => bbb(7 downto 4));
    src4 : component source
        generic map(n => 7)
        port map(output => ccc);
    src5 : component source
        generic map(n => 7)
        port map(output => ddd);
end;

