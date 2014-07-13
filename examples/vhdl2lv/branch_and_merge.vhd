library ieee;
use ieee.std_logic_1164.all;

entity branch_and_merge is
end entity;

architecture behavioral of branch_and_merge is
  signal aaa : std_logic_vector(7 downto 0);
  signal bbb : std_logic_vector(7 downto 0);
  signal ccc : std_logic_vector(7 downto 0);
  signal ddd : std_logic_vector(7 downto 0);
  component sink is
    generic(n   : in  integer := 4);
    port(input  : in  std_logic_vector(n downto 0));
  end component;
  component source is
    generic(n   : in  integer := 4);
    port(output : out std_logic_vector(n downto 0));
  end component;
begin
  -- The "component" token is optional for component instantiation.
  src0 : component source port map(aaa(3 downto 0));
  src1 : source port map(aaa(7 downto 4));
  dst0 : sink port map(aaa(3 downto 0));
  dst1 : sink port map(aaa(7 downto 4));

  src2 : source port map(bbb(3 downto 0));
  src3 : source port map(bbb(7 downto 4));
  dst2 : sink generic map(7) port map(bbb);

  src4 : source generic map(7) port map(ccc);
  dst3 : sink port map(ccc(3 downto 0));
  dst4 : sink port map(ccc(7 downto 4));

  src5 : source generic map(7) port map(ddd);
  dst5 : sink generic map(7) port map(ddd);
end behavioral;
